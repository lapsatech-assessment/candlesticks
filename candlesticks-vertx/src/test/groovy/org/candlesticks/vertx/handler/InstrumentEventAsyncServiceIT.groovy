package org.candlesticks.vertx.handler

import static org.candlesticks.model.Type.ADD
import static org.candlesticks.model.Type.DELETE
import static org.candlesticks.model.Type.STATUS
import static utils.VertxTestUtils.waitValue

import org.candlesticks.model.Instrument
import org.candlesticks.model.InstrumentEvent
import org.candlesticks.model.InstrumentEventResponse
import org.candlesticks.model.Isin
import org.candlesticks.model.Length
import org.candlesticks.vertx.handler.InstrumentEventAsyncService

import io.vertx.core.Promise
import utils.VertxAwareSpecification

class InstrumentEventAsyncServiceIT extends VertxAwareSpecification {

  def 'test handle add/delete/status instrument events'() {

    given:
    def lengths = [
      Length.parse('PT10S'),
      Length.parse('PT20S')
    ] as Set
    def service = InstrumentEventAsyncService.instance(lengths)


    when: 'Received instrument event for adding instrument to the stream'
    def instrumentEvent1 = new InstrumentEvent(type: ADD, data: new Instrument(isin: Isin.of('ABC123')))
    def p1 = Promise.<InstrumentEventResponse>promise()
    vertx.runOnContext { service.callAsync(instrumentEvent1).onComplete(p1) }
    def instrumentEventResponse1 = waitValue(p1)

    then:
    noExceptionThrown()

    and:
    instrumentEventResponse1.event == instrumentEvent1
    instrumentEventResponse1.lengthsAvailable == lengths

    and: 'a number of the deployed generator verticle instances should be equals to the number of defined lengths'
    vertx.deploymentIDs().size() == 2


    when: 'Received isntrument event for adding instrument to the stream for instrumen that is already in the stream'

    def instrumentEvent2 = new InstrumentEvent(type: ADD, data: new Instrument(isin: Isin.of('ABC123')))
    def p2 = Promise.<InstrumentEventResponse>promise()
    vertx.runOnContext { service.callAsync(instrumentEvent2).onComplete(p2) }
    def instrumentEventResponse2 = waitValue(p2)

    then:
    noExceptionThrown()

    and:
    instrumentEventResponse2.event == instrumentEvent2
    instrumentEventResponse2.lengthsAvailable == lengths

    and: 'number of the deployed worker verticles should not change'
    vertx.deploymentIDs().size() == 2


    when: 'Received instrument event for status'

    def instrumentEvent3 = new InstrumentEvent(type: STATUS, data: new Instrument(isin: Isin.of('ABC123')))
    def p3 = Promise.<InstrumentEventResponse>promise()
    vertx.runOnContext { service.callAsync(instrumentEvent3).onComplete(p3) }
    def instrumentEventResponse3 = waitValue(p3)

    then:
    noExceptionThrown()

    and:
    instrumentEventResponse3.event == instrumentEvent3
    instrumentEventResponse3.lengthsAvailable == lengths


    when: 'Received instrument event for removing instrument from the stream'

    def instrumentEvent4 = new InstrumentEvent(type: DELETE, data: new Instrument(isin: Isin.of('ABC123')))
    def p4 = Promise.<InstrumentEventResponse>promise()
    vertx.runOnContext { service.callAsync(instrumentEvent4).onComplete(p4) }
    def instrumentEventResponse4 = waitValue(p4)

    then:
    noExceptionThrown()

    and: 'received response with no lenght data'
    instrumentEventResponse4.event == instrumentEvent4
    instrumentEventResponse4.lengthsAvailable == [] as Set

    and: 'should be no verticle deployed anymore'
    vertx.deploymentIDs().size() == 0


    when: 'Received instrument event for removing instrument from the stream that is not exists'

    def instrumentEvent5 = new InstrumentEvent(type: DELETE, data: new Instrument(isin: Isin.of('ABC123')))
    def p5 = Promise.<InstrumentEventResponse>promise()
    vertx.runOnContext { service.callAsync(instrumentEvent5).onComplete(p5) }
    def instrumentEventResponse5 = waitValue(p5)

    then:
    noExceptionThrown()

    and: 'received response with no lenght data'
    instrumentEventResponse5.event == instrumentEvent5
    instrumentEventResponse5.lengthsAvailable == [] as Set

    and: 'should be no verticle deployed still'
    vertx.deploymentIDs().size() == 0
  }
}
