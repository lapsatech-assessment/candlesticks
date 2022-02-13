package org.candlesticks.vertx.handler.impl

import org.candlesticks.model.Isin
import org.candlesticks.model.Price
import org.candlesticks.model.Quote
import org.candlesticks.model.QuoteEvent
import org.candlesticks.vertx.service.EventBusAccess

import spock.lang.Specification

class QuoteEventHandlerImplTest extends Specification {

  def 'test that handler send prices via eventbus'() {
    given:
    def eventBusAccess = Mock(EventBusAccess)
    def handler = new QuoteEventHandlerImpl(eventBusAccess)

    when:
    handler.handle(new QuoteEvent(data: new Quote(isin: Isin.of('ABC123'), price: Price.of(10d))))

    then:
    noExceptionThrown()

    and:
    1 * eventBusAccess.publishPrice(Isin.of('ABC123'), Price.of(10d))
  }
}
