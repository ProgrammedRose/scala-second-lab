import scala.io.StdIn

object Core {

  def runCarlo(): Unit = {
    
    // Грязная функция... а куда деваться
    def askUser(prompt: String): Int = {
      print(prompt)
      StdIn.readInt()
    }

  val maxPointsNumber = Int.MaxValue

  val pointsNumber = askUser("Please enter pointsNumber: ")
  val threadsNumber = askUser("Please enter threadsNumber: ")
  val repeats = askUser("Please enter repeats: ")

  println("\nThank you, my dear!")

  println("\nOne time calculated pi: ")
  val pi = MonteCarloMonteCristo.piCristo(pointsNumber = maxPointsNumber,
    threadsNumber = threadsNumber)
  println(s"pi ≈ $pi")

  println("\nMore than one time calculated pi (averaged): ")
  val avgPi = averagedPiMonteCarlo(pointsNumber = maxPointsNumber,
    threadsNumber = threadsNumber,
    repeats = repeats)

  println(s"avg pi ≈ $avgPi")

  println("\nTrue value of pi to compare: ")
  println("true pi = " + math.Pi)
}
  
}
