object GraphParser {
  def parse(file : String) = {
    val source = scala.io.Source.fromFile(file)
    val lines = try source.getLines().toList finally source.close()
    lines.map(s => s.split(" "))
      .zip(for (i <- lines.indices) yield i)
      .map(p => (p._1.filter(s => s != ""), p._2))
      .map(p => if (p._1.isEmpty) Array[Int]() else p._1.map(s => s.toInt)).toArray
  }
}
