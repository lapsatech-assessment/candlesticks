package org.candlesticks.vertx.verticles


import static utils.VertxTestUtils.waitComplete
import static utils.VertxTestUtils.waitValue

import java.time.Duration

import org.candlesticks.model.CandlestickEvent
import org.candlesticks.model.Isin
import org.candlesticks.model.Length
import org.candlesticks.vertx.service.EventBusAccess

import io.vertx.core.Promise
import utils.VertxAwareSpecification

class CandlestickGeneratorInstanceVerticleIT extends VertxAwareSpecification {

  def 'test verticle is started'() {
    given:
    def isin = Isin.of('ABC123')
    def length = Length.of(Duration.ofMillis(50))
    def deploymentIdPromise = Promise.<String>promise()
    def priceSentPromise = Promise.<Void>promise()
    def candlestickEventPromise = Promise.<CandlestickEvent>promise()

    when:
    vertx.runOnContext {
      vertx.deployVerticle(new CandlestickGeneratorInstanceVerticle(isin, length))
          .onComplete(deploymentIdPromise)

      EventBusAccess.instance().publishPrice(isin, 10d).onComplete(priceSentPromise)
      EventBusAccess.instance().registerCandlestickEventStub { candlestickEvent -> candlestickEventPromise.tryComplete(candlestickEvent) }
    }
    def deploymentId = waitValue(deploymentIdPromise, 100)
    waitComplete(priceSentPromise, 100)
    def candlestickEvent = waitValue(candlestickEventPromise, 1000)

    then:
    deploymentId
    candlestickEvent
    candlestickEvent.isin == isin
    candlestickEvent.length == length
  }
}
