package org.candlesticks.server.core

import static java.lang.Double.NaN

import java.time.Duration
import java.time.Instant

import org.candlesticks.core.CandlestickPeriod

import spock.lang.Specification

class CandlestickPeriodTest extends Specification {

  def 'test next generation'() {

    when:
    def period = CandlestickPeriod.of(Instant.parse('2022-02-01T04:00:00Z'), Duration.ofMinutes(1));

    then:
    period.fromInclusive == Instant.parse('2022-02-01T04:00:00Z')
    period.toExclusive == Instant.parse('2022-02-01T04:01:00Z')
    
    when:
    def next = period.next()
    
    then:
    next
    next.fromInclusive == Instant.parse('2022-02-01T04:01:00Z')
    next.toExclusive == Instant.parse('2022-02-01T04:02:00Z')
  }

  def 'test inclusive'() {
    when:
    def period = CandlestickPeriod.of(Instant.parse('2022-02-01T04:00:00Z'), Duration.ofMinutes(1));

    then:
    !period.isFuture(Instant.parse('2022-02-01T03:59:59Z'))
    !period.isFuture(Instant.parse('2022-02-01T04:00:00Z'))
    !period.isFuture(Instant.parse('2022-02-01T04:00:59Z'))
    period.isFuture(Instant.parse('2022-02-01T04:01:00Z'))
    period.isFuture(Instant.parse('2022-02-01T04:01:01Z'))
    
  }
}
