import java.util.SplittableRandom
import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}

object MonteCarloMonteCristo {

  // Функция плюс-минус равномерно разбивает количество точек на количество потоков
  private def splitCounts(total: Int, parts: Int): List[Int] = {
    val base = total / parts        // тут целое количество частей
    val remaining  = total % parts  // тут остаток

    (0 until parts).toList.map(i => base + (if (i < remaining) 1 else 0)) //
  }


  // Функция, что генерирует n точек и считает попадания по предикату
  // (тут баловство с каррированием и предикатами)
  private def countHits(n: Int)(predicate: SplittableRandom => Boolean): Long = {
    val random = new SplittableRandom() // локальный рандом для потока
    var i = 0
    var hits = 0L
    while (i < n) {
      if (predicate(random))
        hits += 1
      i += 1
    }
    hits // возвращаем попадания
  }



  // Самая главная функция
  def piCristo(pointsNumber: Int, threadsNumber: Int): Double = {

    // делаем проверочку
    require(pointsNumber > 0, "pointsNumber > 0")
    require(threadsNumber >= 1, "threadsNumber >= 1")

    val counts = splitCounts(pointsNumber, threadsNumber)

    val executor = Executors.newFixedThreadPool(threadsNumber)
    implicit val ec = ExecutionContext.fromExecutorService(executor)

    try {
      val futures: List[Future[Long]] = counts.map { n =>
        Future {
          countHits(n) ( rnd =>
            // точка в квадрате [-1,1]x[-1,1]
            val x = rnd.nextDouble() * 2.0 - 1.0
            val y = rnd.nextDouble() * 2.0 - 1.0

            // проверяем и возвращаем (попали или про***лись)
            x * x + y * y <= 1.0
          )
        }
      }

      // собираем полученные значения
      val aggregated = Future.sequence(futures).map(_.foldLeft(0L)(_ + _))
      val totalHits = Await.result(aggregated, Duration.Inf)

      // возвращаем примерное значение pi согласно методу (это из pi/4 = hits/points)
      4.0 * totalHits.toDouble / pointsNumber.toDouble

    } finally {

      // убиваем экзикутора
      executor.shutdown()
      executor.awaitTermination(5, TimeUnit.SECONDS)
    }
  }

}
