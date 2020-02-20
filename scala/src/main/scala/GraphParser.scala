object GraphParser {
  def parse(file : String) = {
    val source = scala.io.Source.fromFile(file)
    val lines = try source.mkString finally source.close()
    lines.split("\n").map(s => s.split(" ")).map(a => a.map(s => s.toInt))
  }
}
