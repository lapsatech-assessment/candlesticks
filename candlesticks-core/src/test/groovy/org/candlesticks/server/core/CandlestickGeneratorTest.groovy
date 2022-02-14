package org.candlesticks.server.core

import static java.time.Instant.parse
import static org.candlesticks.model.Price.of

import java.time.Duration
import java.util.function.Consumer

import org.candlesticks.core.CandlestickGenerator
import org.candlesticks.model.Candlestick
import org.candlesticks.model.CandlestickEvent
import org.candlesticks.model.Isin
import org.candlesticks.model.Length

import spock.lang.Specification

class CandlestickGeneratorTest extends Specification {

  def 'test that all expected candlesticks are generated according to prices and timestamps'() {
    given:
    def handler = Stub(Consumer)
    def results = [] as List<CandlestickEvent>
    handler.accept(_) >>  { results << it[0] }
    
    def length = Length.of(Duration.ofMinutes(1))
    def isin = Isin.of("ABC123")
    
    def p = new CandlestickGenerator(parse('2022-02-01T04:00:00Z'), length, isin, handler)

    when:
    p.tick(parse('2022-02-01T04:00:10Z'), 10d)
    p.tick(parse('2022-02-01T04:00:30Z'), 30d)
    p.tick(parse('2022-02-01T04:00:50Z'), 50d)
    p.tick(parse('2022-02-01T04:01:10Z'))
    p.tick(parse('2022-02-01T04:01:30Z'), 130d)
    p.tick(parse('2022-02-01T04:02:00Z'))
    p.tick(parse('2022-02-01T04:04:00Z'))

    then:
    results
    results.size() == 4

    and:
    results.forEach {
      assert it.isin == isin
      assert it.length == length
    } 

    and:
    verifyAll(results[0].candlestick) {
      openTimestamp == parse('2022-02-01T04:00:00Z')
      closeTimestamp == parse('2022-02-01T04:01:00Z')
      openPrice == of(10d)
      highPrice == of(50d)
      lowPrice == of(10d)
      closingPrice == of(50d)
      amount == 3
    }

    and:
    verifyAll(results[1].candlestick) {
      openTimestamp == parse('2022-02-01T04:01:00Z')
      closeTimestamp == parse('2022-02-01T04:02:00Z')
      openPrice == of(130d)
      highPrice == of(130d)
      lowPrice == of(130d)
      closingPrice == of(130d)
      amount == 1
    }

    and:
    verifyAll(results[2].candlestick) {
      openTimestamp == parse('2022-02-01T04:02:00Z')
      closeTimestamp == parse('2022-02-01T04:03:00Z')
      !openPrice
      !highPrice
      !lowPrice
      !closingPrice
      amount == 0
    }

    and:
    verifyAll(results[3].candlestick) {
      openTimestamp == parse('2022-02-01T04:03:00Z')
      closeTimestamp == parse('2022-02-01T04:04:00Z')
      !openPrice
      !highPrice
      !lowPrice
      !closingPrice
      amount == 0
    }
  }
}
