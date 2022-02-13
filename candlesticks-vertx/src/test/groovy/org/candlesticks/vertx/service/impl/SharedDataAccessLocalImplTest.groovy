package org.candlesticks.vertx.service.impl

import static utils.VertxTestUtils.waitValue

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.shareddata.Lock
import io.vertx.core.shareddata.SharedData
import spock.lang.Specification

class SharedDataAccessLocalImplTest extends Specification {

  def 'test get candlestick map according to given name pattern'() {
    given:
    def sharedData = Mock(SharedData)

    def vertx = Stub(Vertx)
    vertx.sharedData() >> sharedData

    def asyncMap = Stub(AsyncMap)

    def service = new SharedDataAccessLocalImpl(vertx, 'base', '/')

    when:
    def mapFuture = service.getCandlestickCollectionMap()

    then:
    1 * sharedData.getLocalAsyncMap('base/maps/candlestick-collection') >> Future.succeededFuture(asyncMap)

    and:
    mapFuture

    when:
    def map = waitValue(mapFuture)

    then:
    map == asyncMap
  }

  def 'test accquire candlestick write lock according to given name pattern'() {
    given:
    def sharedData = Mock(SharedData)

    def vertx = Stub(Vertx)
    vertx.sharedData() >> sharedData

    def lock = Stub(Lock)

    def service = new SharedDataAccessLocalImpl(vertx, 'base', '/')

    when:
    def lockFuture = service.accquireCandlestickCollectionWriteLock()

    then:
    1 * sharedData.getLocalLock('base/write-locks/candlestick-collection') >> Future.succeededFuture(lock)

    and:
    lockFuture

    when:
    def resultLock = waitValue(lockFuture)

    then:
    lock == resultLock
  }
}
