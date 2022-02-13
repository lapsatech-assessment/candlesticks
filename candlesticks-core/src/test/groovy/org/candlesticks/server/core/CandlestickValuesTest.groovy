package org.candlesticks.server.core

import org.candlesticks.core.CandlestickValues

import spock.lang.Specification

class CandlestickValuesTest extends Specification {

  def 'test wrong args'() {
    when:
    CandlestickValues.of(1d, 2d, 3d, null, 0)

    then:
    thrown(NullPointerException)

    when:
    CandlestickValues.of(1d, 2d, null, 4d, 0)

    then:
    thrown(NullPointerException)

    when:
    CandlestickValues.of(1, null, 3d, 4d, 0)

    then:
    thrown(NullPointerException)

    when:
    CandlestickValues.of(null, 2d, 3d, 4d, 0)

    then:
    thrown(NullPointerException)
  }

  def 'test general operations'() {

    when:
    def values = CandlestickValues.empty()

    then:
    values.empty
    values.openPrice == null
    values.closingPrice  == null
    values.highPrice == null
    values.lowPrice  == null
    values.amount == 0

    when:
    values = values.acceptPrice(10d)

    then:
    !values.empty
    values.openPrice == 10d
    values.closingPrice == 10d
    values.highPrice == 10d
    values.lowPrice == 10d
    values.amount == 1

    when:
    values = values.acceptPrice(15d)

    then:
    !values.empty
    values.openPrice == 10d
    values.closingPrice == 15d
    values.highPrice == 15d
    values.lowPrice == 10d
    values.amount == 2

    when:
    values = values.acceptPrice(20d)

    then:
    !values.empty
    values.openPrice == 10d
    values.closingPrice == 20d
    values.highPrice == 20d
    values.lowPrice == 10d
    values.amount == 3

    when:
    values = values.acceptPrice(0d)

    then:
    !values.empty
    values.openPrice == 10d
    values.closingPrice == 0d
    values.highPrice == 20d
    values.lowPrice == 0d
    values.amount == 4
  }
}
