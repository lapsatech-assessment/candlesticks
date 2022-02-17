package org.candlesticks.server.core.impl

import static java.lang.Double.NaN
import static java.time.Instant.parse

import java.time.Duration
import java.time.Instant

import org.candlesticks.core.impl.CandlestickPeriod

import spock.lang.Specification

class CandlestickPeriodTest extends Specification {

  def 'test next generation'() {

    when:
    def period = CandlestickPeriod.of(parse('2022-02-01T04:00:00Z'), Duration.ofMinutes(1));

    then:
    period.fromInclusive == parse('2022-02-01T04:00:00Z')
    period.toExclusive == parse('2022-02-01T04:01:00Z')

    when:
    def next = period.next()
    
    then:
    next
    next.fromInclusive == parse('2022-02-01T04:01:00Z')
    next.toExclusive == parse('2022-02-01T04:02:00Z')
  }

  def 'test isInstantInFuture'() {
    when:
    def period = CandlestickPeriod.of(parse('2022-02-01T04:00:00Z'), Duration.ofMinutes(1));

    then:
    !period.isInstantInFuture(parse('2022-02-01T03:59:59Z'))
    !period.isInstantInFuture(parse('2022-02-01T04:00:00Z'))
    !period.isInstantInFuture(parse('2022-02-01T04:00:59Z'))
    period.isInstantInFuture(parse('2022-02-01T04:01:00Z'))
    period.isInstantInFuture(parse('2022-02-01T04:01:01Z'))
  }

  def 'test isInstantInPast'() {
    when:
    def period = CandlestickPeriod.of(parse('2022-02-01T04:00:00Z'), Duration.ofMinutes(1));

    then:
    period.isInstantInPast(parse('2022-02-01T03:59:59Z'))
    !period.isInstantInPast(parse('2022-02-01T04:00:00Z'))
    !period.isInstantInPast(parse('2022-02-01T04:00:59Z'))
    !period.isInstantInPast(parse('2022-02-01T04:01:00Z'))
  }

  
  def 'test isInstantInPeriod'() {
    when:
    def period = CandlestickPeriod.of(parse('2022-02-01T04:00:00Z'), Duration.ofMinutes(1));

    then:
    !period.isInstantInPeriod(parse('2022-02-01T03:59:59Z'))
    period.isInstantInPeriod(parse('2022-02-01T04:00:00Z'))
    period.isInstantInPeriod(parse('2022-02-01T04:00:59Z'))
    !period.isInstantInPeriod(parse('2022-02-01T04:01:00Z'))
  }
}
