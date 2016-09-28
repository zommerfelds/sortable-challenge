package challenge

import java.io.PrintWriter

import scala.io.Source
import play.api.libs.json._


object Main extends App {
  if (args.size != 3) {
    println("usage:\n\tsbt \"run data/products.txt data/listings.txt results.txt\"")
    sys.exit(1)
  }
  val Array(productsPath, listingsPath, outputPath) = args

  def readJsonLines(fileName: String) =
    Source.fromFile(fileName).getLines().map(Json.parse(_).as[Map[String, String]])

  // remove hyphens and make lowercase
  def simplify[A](m: Map[A, String]) = m.mapValues(v => v.filter(_ != '-').toLowerCase().trim)

  // read JSON lines into Map objects
  val products = readJsonLines(productsPath).toVector
  val productsSimplified = products.map(simplify)
  val listings = readJsonLines(listingsPath).toVector
  val listingsSimplified = listings.map(simplify)
  val listingsTitleTokens = listingsSimplified.zipWithIndex.map(t => t._2 -> t._1("title").split(" "))

  // remove duplicate products because we risk losing precision depending on how ground truth is defined (only 1 case)
  val productsUnique = productsSimplified.groupBy(p => (p("model"), p("manufacturer"), p.get("family")))
    .collect{ case (g, xs) if xs.size == 1 => xs.head }.toVector

  // add model version without spaces so we can match single words
  val productsExtra = productsUnique.map(p => p.updated("modelShort", p("model").filter(_ != ' ')))

  // match the listings to products
  val listingMatches = listingsTitleTokens.map{
    case (li, t) => li -> productsExtra.zipWithIndex.filter { case (p, pi) =>
      (t.contains(p("model")) || t.contains(p("modelShort"))) &&
        p.get("family").forall(t.contains) &&
        listingsSimplified(li)("manufacturer").contains(p("manufacturer"))
    }.map(_._2)}

  // only take the listings that match to exactly one product
  val singleMatches = listingMatches.collect{case (l, ps) if ps.size == 1 => (l, products(ps.head))}

  // simple histogram (used for plot)
  // println(listingMatches.groupBy(_._2.size).mapValues(_.size))

  // write to file
  val pw = new PrintWriter(outputPath)
  for (g <- singleMatches.groupBy(_._2)) {
    pw.write(JsObject(Map(
      "product_name" -> JsString(g._1("product_name")),
      "listings" -> JsArray(g._2.map(t => Json.toJson(listings(t._1))))
    )).toString + '\n')
  }
  pw.close
}