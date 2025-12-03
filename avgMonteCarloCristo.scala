import MonteCarloMonteCristo.piCristo

import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*

// Здесь реализована идея "а что если посчитать pi M раз и усреднить"
def averagedPiMonteCarlo(pointsNumber: Int,
                         threadsNumber: Int,
                         repeats: Int
                        ): Double = {

  // здесь создаем последовательность из фьючеров (их тут в количестве = repeats)
  val futures: Seq[Future[Double]] =
    (1 to repeats).map { _ =>
      Future {
        piCristo(pointsNumber, threadsNumber)
      }
    }

  // собираем результаты в последовательность и усредняем
  val aggregated: Future[Double] =
    Future.sequence(futures).map { results =>
      results.sum / repeats
    }

// отдаем результат
  Await.result(aggregated, Duration.Inf)
}
