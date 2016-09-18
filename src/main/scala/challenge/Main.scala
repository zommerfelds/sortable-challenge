package challenge

import scala.io.Source
import play.api.libs.json._

import scala.collection.immutable.HashMap
import scala.util.Random

// http://sortable.com/challenge/

object Main extends App {

  def readJsonLines(fileName: String) =
    Source.fromFile(fileName).getLines().map(Json.parse(_).as[Map[String, String]])

  // remove hyphen and make lowercase
  def simplify[A](m: Map[A, String]) = m.mapValues(v => v.filter(_ != '-').toLowerCase())

  // read JSON lines into Map objects
  println("reading data")
  val products = readJsonLines("data/products.txt").map(simplify).toVector
  val listings = readJsonLines("data/listings.txt").map(simplify).toVector

  // check if products are unique: NO!
  // remove duplicates because we risk losing precision depending on how ground truth is defined
  println("removing duplicate products")
  val productsUnique = products.groupBy(p => (p("model"), p("manufacturer"), p.get("family")))
    .collect{ case (g, xs) if xs.size == 1 => xs.head }.toVector

  println("shortening model name")
  val productsExtra = productsUnique.map(p => p.updated("modelShort", p("model").filter(_ != ' ')))

  println("finding matches")
  val x = listings.map(l => l -> productsExtra.zipWithIndex.collect{
    case (p, i) if (l("title").contains(p("model")) || l("title").contains(p("modelShort")))
                //&& l("title").contains(p("manufacturer"))
                && l("title").contains(p.getOrElse("family", ""))
                && l("manufacturer").contains(p("manufacturer"))
      => i
  })

  /* product types:
   - real camera
     => try to match perfectly
   - camera accessories (e.g. flash, tripod, battery)
     => these could be compatible with different camera models
     => ignore because we only want cameras
   */

  for (i <- 0 until 5)
    println(i + ": " + x.filter(_._2.size == i).size / x.size.toDouble)
  //for (t <- x.filter(_._2.size == 0).take(50).map(_._1))
  /*Random.setSeed(0)
  for (t <- Random.shuffle(x.filter(e => e._2.size == 1 && !e._1("manufacturer").contains(e._2(0)("manufacturer"))).take(150)))
    println(t)*/
}