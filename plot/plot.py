import matplotlib.pyplot as plt

data = [13717, 6471, 7, 1]

bar_width = 0.8
rects = plt.bar(range(len(data)), data, bar_width, color='c')

max_height = rects[0].get_height()
for rect in rects:
    height = rect.get_height()
    plt.axes().text(
        rect.get_x() + rect.get_width()/2., height,
        '{} ({:.1f}%)'.format(int(height), height / sum(data) * 100), ha='center', va='bottom')

plt.xticks(list(map(lambda x: x + bar_width/2, range(0, len(data)))), map(str, range(len(data))))
plt.xlabel('Listing frequency')
plt.ylabel('Number of assigned products')
plt.xlim([-0.3, len(data) - 1 + bar_width + 0.3])
plt.ylim([0, max_height * 1.07])
plt.title('Histogram of number of matched products:\nOnly listings assigned to only one product are kept', y=1.04)

plt.savefig('plot.png', bbox_inches='tight')