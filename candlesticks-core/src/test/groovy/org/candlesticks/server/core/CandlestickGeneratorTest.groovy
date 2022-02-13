package org.candlesticks.server.core

import java.time.Duration
import java.time.Instant
import java.util.function.Consumer

import org.candlesticks.core.CandlestickGenerator
import org.candlesticks.model.Candlestick
import org.candlesticks.model.CandlestickEvent
import org.candlesticks.model.Isin
import org.candlesticks.model.Length
import org.candlesticks.model.Price

import spock.lang.Specification

class CandlestickGeneratorTest extends Specification {

  def 'test process'() {
    given:
    def handler = Mock(Consumer) as Consumer<Candlestick>
    def p = new CandlestickGenerator(Instant.parse('2022-02-01T04:00:00Z'), Length.of(Duration.ofMinutes(1)), Isin.of("ABC123"), handler)

    when:
    p.tick(Instant.parse('2022-02-01T04:00:00Z'), 11d)

    then:
    0 * handler.accept(_)

    when:
    p.tick(Instant.parse('2022-02-01T04:00:01Z'), 20d)

    then:
    0 * handler.accept(_)

    when:
    p.tick(Instant.parse('2022-02-01T04:00:02Z'), 15d)

    then:
    0 * handler.accept(_)

    when:
    p.tick(Instant.parse('2022-02-01T04:01:00Z'), 1d)

    then:
    1 * handler.accept({ CandlestickEvent event ->
      Candlestick candlestick = event.candlestick
      candlestick.openTimestamp == Instant.parse('2022-02-01T04:00:00Z')
      candlestick.closeTimestamp == Instant.parse('2022-02-01T04:01:00Z')
      candlestick.highPrice == Price.of(20d)
      candlestick.lowPrice == Price.of(11d)
      candlestick.openPrice == Price.of(11d)
      candlestick.closingPrice == Price.of(15d)
      candlestick.amount == 3
    })

    when:
    p.tick(Instant.parse('2022-02-01T04:02:00Z'), 55d)

    then:
    1 * handler.accept({ CandlestickEvent event ->
      Candlestick candlestick = event.candlestick
      candlestick.openTimestamp == Instant.parse('2022-02-01T04:01:00Z')
      candlestick.closeTimestamp == Instant.parse('2022-02-01T04:02:00Z')
      candlestick.highPrice.value == 1d
      candlestick.lowPrice.value == 1d
      candlestick.openPrice.value == 1d
      candlestick.closingPrice.value == 1d
      candlestick.amount == 1
    })
    0 * handler.accept(_)

    when:
    p.tick(Instant.parse('2022-02-01T04:05:00Z'), 15d)

    then:
    1 * handler.accept({ CandlestickEvent event ->
      Candlestick candlestick = event.candlestick
      candlestick.openTimestamp == Instant.parse('2022-02-01T04:02:00Z')
      candlestick.closeTimestamp == Instant.parse('2022-02-01T04:03:00Z')
      candlestick.highPrice == Price.of(55d)
      candlestick.lowPrice == Price.of(55d)
      candlestick.openPrice == Price.of(55d)
      candlestick.closingPrice == Price.of(55d)
      candlestick.amount == 1
    })

    then:
    1 * handler.accept({ CandlestickEvent event ->
      Candlestick candlestick = event.candlestick
      candlestick.openTimestamp == Instant.parse('2022-02-01T04:03:00Z')
      candlestick.closeTimestamp == Instant.parse('2022-02-01T04:04:00Z')
      candlestick.highPrice == null
      candlestick.lowPrice == null
      candlestick.openPrice == null
      candlestick.closingPrice == null
      candlestick.amount == 0
    })

    then:
    1 * handler.accept({ CandlestickEvent event ->
      Candlestick candlestick = event.candlestick
      candlestick.openTimestamp == Instant.parse('2022-02-01T04:04:00Z')
      candlestick.closeTimestamp == Instant.parse('2022-02-01T04:05:00Z')
      candlestick.highPrice == null
      candlestick.lowPrice == null
      candlestick.openPrice == null
      candlestick.closingPrice == null
      candlestick.amount == 0
    })
    0 * handler.accept(_)
  }
}
