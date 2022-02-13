package org.candlesticks.vertx.handler.impl

import java.time.Duration
import java.time.Instant

import org.candlesticks.model.Candlestick
import org.candlesticks.model.CandlestickEvent
import org.candlesticks.model.Isin
import org.candlesticks.model.Length
import org.candlesticks.vertx.service.CandlestickAsyncRepository

import io.vertx.core.Future
import spock.lang.Specification

class CandlestickEventHandlerImplTest extends Specification {

  def 'test that handler adds new candlestick to the collecton and trim obsolete candlesticks'() {
    given:

    def candlestickToAdd = new Candlestick(closeTimestamp: Instant.parse('2020-02-10T10:05:00Z'))

    def initial = [
      new Candlestick(closeTimestamp: Instant.parse('2020-02-10T10:02:00Z')),
      new Candlestick(closeTimestamp: Instant.parse('2020-02-10T10:03:00Z')),
      new Candlestick(closeTimestamp: Instant.parse('2020-02-10T10:04:00Z'))
    ]

    def expectedResult = [
      new Candlestick(closeTimestamp: Instant.parse('2020-02-10T10:04:00Z')),
      new Candlestick(closeTimestamp: Instant.parse('2020-02-10T10:05:00Z'))
    ]

    def candlestickRepository = Mock(CandlestickAsyncRepository)
    def candlestickTimeToLive = Duration.ofMinutes(1)
    def now = Instant.parse('2020-02-10T10:05:00Z')
    def service = new CandlestickEventHandlerImpl(candlestickRepository, candlestickTimeToLive, { now })

    when:
    service.handle(new CandlestickEvent(candlestickToAdd, Isin.of('ABC123'), Length.parse('PT10S')))

    then:
    1 * candlestickRepository.amendCollection(_, _) >> { args ->
      def key = args[0]
      assert key.isin() ==  Isin.of('ABC123')
      assert key.length() ==  Length.parse('PT10S')
      def result = args[1].apply(initial)
      assert result == expectedResult
      return Future.succeededFuture()
    }
  }

  def 'test that handler creates collection if null'() {
    given:
    def candlestick = new Candlestick(closeTimestamp: Instant.parse('2020-02-10T10:05:00Z'))
    def expectedResult = [candlestick]

    def candlestickRepository = Mock(CandlestickAsyncRepository)
    def candlestickTimeToLive = Duration.ofMinutes(1)
    def now = Instant.parse('2020-02-10T10:05:00Z')
    def service = new CandlestickEventHandlerImpl(candlestickRepository, candlestickTimeToLive, { now })

    when:
    service.handle(new CandlestickEvent(isin: Isin.of('ABC123'), length: Length.parse('PT10S'), candlestick: candlestick))

    then:
    1 * candlestickRepository.amendCollection(_, _) >> { args ->
      def result = args[1].apply(null)
      assert result
      assert result == expectedResult
      return Future.succeededFuture()
    }
  }
}
