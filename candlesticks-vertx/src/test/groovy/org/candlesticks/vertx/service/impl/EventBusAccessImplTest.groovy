package org.candlesticks.vertx.service.impl

import static org.candlesticks.model.Type.ADD
import static utils.VertxTestUtils.waitComplete
import static utils.VertxTestUtils.waitValue

import org.candlesticks.model.Candlestick
import org.candlesticks.model.CandlestickEvent
import org.candlesticks.model.InstrumentEvent
import org.candlesticks.model.InstrumentEventResponse
import org.candlesticks.model.Isin
import org.candlesticks.model.Quote
import org.candlesticks.model.QuoteEvent
import org.candlesticks.vertx.service.EventBusServiceTransport

import io.vertx.core.Future
import io.vertx.core.Handler
import local.vertx.core.AsyncService
import spock.lang.Specification

class EventBusAccessImplTest extends Specification {


  def 'test registerPriceStub'() {
    given:

    def transport = Mock(EventBusServiceTransport)
    def stub = Stub(Handler)

    def isin = Isin.of('ABC123')

    def service = new EventBusAccessImpl('base', '/', transport)

    when:
    def future = service.registerPriceStub(isin, stub)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.registerStub('base/service/price/ABC123', Double, stub) >> Future.succeededFuture()
  }

  def 'test publishPrice'() {
    given:

    def transport = Mock(EventBusServiceTransport)

    def service = new EventBusAccessImpl('base', '/', transport)

    def isin = Isin.of('ABC123')
    def price = 123d

    when:
    def future = service.publishPrice(isin, price)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.publish('base/service/price/ABC123', price) >> Future.succeededFuture()
  }

  //

  def 'test registerInstrumentsEventService'() {
    given:

    def transport = Mock(EventBusServiceTransport)
    def asyncService = Stub(AsyncService)

    def service = new EventBusAccessImpl('base', '/', transport)

    when:
    def future = service.registerInstrumentsEventService(asyncService)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.registerService('base/service/instrument-event',  InstrumentEvent, asyncService) >> Future.succeededFuture()
  }


  def 'test callInstrumentsEventService'() {
    given:

    def transport = Mock(EventBusServiceTransport)
    def stub = Stub(Handler)

    def service = new EventBusAccessImpl('base', '/', transport)

    def event = new InstrumentEvent(type: ADD)
    def responseEvent = new InstrumentEventResponse(event: event)

    when:
    def responseFuture = service.callInstrumentsEventService(event)
    def response = waitValue(responseFuture)

    then:
    noExceptionThrown()

    and:
    1 * transport.callService('base/service/instrument-event',  event, InstrumentEventResponse) >> Future.succeededFuture(new InstrumentEventResponse(event: event))

    and:
    responseEvent == response
  }

  def 'test registerInstrumentsEventStub'() {
    given:

    def transport = Mock(EventBusServiceTransport)
    def stub = Stub(Handler)

    def service = new EventBusAccessImpl('base', '/', transport)

    when:
    def future = service.registerInstrumentsEventStub(stub)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.registerStub('base/service/instrument-event',  InstrumentEvent, stub) >> Future.succeededFuture()
  }

  def 'test sendInstrumentsEvent'() {
    given:

    def transport = Mock(EventBusServiceTransport)

    def service = new EventBusAccessImpl('base', '/', transport)

    def event = new InstrumentEvent(type: ADD)

    when:
    def future = service.sendInstrumentsEvent(event)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.send('base/service/instrument-event', event) >> Future.succeededFuture()
  }

  def 'test publishInstrumentsEvent'() {
    given:

    def transport = Mock(EventBusServiceTransport)

    def service = new EventBusAccessImpl('base', '/', transport)

    def event = new InstrumentEvent(type: ADD)

    when:
    def future = service.publishInstrumentsEvent(event)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.publish('base/service/instrument-event', event) >> Future.succeededFuture()
  }

  //

  def 'test registerCandlestickEventStub'() {
    given:

    def transport = Mock(EventBusServiceTransport)
    def stub = Stub(Handler)

    def service = new EventBusAccessImpl('base', '/', transport)

    when:
    def future = service.registerCandlestickEventStub(stub)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.registerStub('base/service/candlestick-event',  CandlestickEvent, stub) >> Future.succeededFuture()
  }

  def 'test sendCandlestickEvent'() {
    given:

    def transport = Mock(EventBusServiceTransport)

    def service = new EventBusAccessImpl('base', '/', transport)

    def event = new CandlestickEvent(candlestick: new Candlestick(amount: 10))

    when:
    def future = service.sendCandlestickEvent(event)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.send('base/service/candlestick-event', event) >> Future.succeededFuture()
  }

  def 'test publishCandlestickEvent'() {
    given:

    def transport = Mock(EventBusServiceTransport)

    def service = new EventBusAccessImpl('base', '/', transport)

    def event = new CandlestickEvent(candlestick: new Candlestick(amount: 10))

    when:
    def future = service.publishCandlestickEvent(event)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.publish('base/service/candlestick-event', event) >> Future.succeededFuture()
  }

  //

  def 'test registerQuoteEventStub'() {
    given:

    def transport = Mock(EventBusServiceTransport)
    def stub = Stub(Handler)

    def service = new EventBusAccessImpl('base', '/', transport)

    when:
    def future = service.registerQuoteEventStub(stub)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.registerStub('base/service/quote-event',  QuoteEvent, stub) >> Future.succeededFuture()
  }

  def 'test publishQuoteEvent'() {
    given:

    def transport = Mock(EventBusServiceTransport)

    def service = new EventBusAccessImpl('base', '/', transport)

    def event = new QuoteEvent(data: new Quote(isin: Isin.of('ABC123')));

    when:
    def future = service.publishQuoteEvent(event)
    waitComplete(future)

    then:
    noExceptionThrown()

    and:
    1 * transport.publish('base/service/quote-event', event) >> Future.succeededFuture()
  }
}