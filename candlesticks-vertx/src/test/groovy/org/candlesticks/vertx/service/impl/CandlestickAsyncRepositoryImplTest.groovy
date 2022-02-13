package org.candlesticks.vertx.service.impl

import static utils.VertxTestUtils.waitComplete
import static utils.VertxTestUtils.waitValue

import java.time.Duration

import org.candlesticks.model.Candlestick
import org.candlesticks.model.Isin
import org.candlesticks.model.Length
import org.candlesticks.vertx.service.SharedDataAccess
import org.candlesticks.vertx.service.CandlestickAsyncRepository.CandlestickCollectionKey

import io.vertx.core.Future
import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.shareddata.Lock
import spock.lang.Specification

class CandlestickAsyncRepositoryImplTest extends Specification {

  def 'test extractCollection method'() {
    given:
    def collection = [
      new Candlestick(amount: 10),
      new Candlestick(amount: 11)
    ]

    def key = CandlestickCollectionKey.of(Isin.of('ABC123'), Length.of(Duration.parse('PT1S')))
    def mapkey = CandlestickAsyncRepositoryImpl.mapKeyString(key)

    def collectionAsyncMap = Mock(AsyncMap)
    def sharedDataAcceess = Mock(SharedDataAccess)
    def repository = new CandlestickAsyncRepositoryImpl(sharedDataAcceess)

    when:
    def colllectionFuture = repository.extractCollection(key)
    def resultCollection = waitValue(colllectionFuture)

    then:
    noExceptionThrown()

    and:
    1 * collectionAsyncMap.get(mapkey) >> Future.succeededFuture(collection)
    1 * sharedDataAcceess.getCandlestickCollectionMap() >> Future.succeededFuture(collectionAsyncMap)

    and:
    resultCollection == collection
  }

  def 'test placeCollectiion method'() {
    given:
    def collection = [
      new Candlestick(amount: 10),
      new Candlestick(amount: 11)
    ]

    def key = CandlestickCollectionKey.of(Isin.of('ABC123'), Length.of(Duration.parse('PT1S')))
    def mapkey = CandlestickAsyncRepositoryImpl.mapKeyString(key)

    def collectionAsyncMap = Mock(AsyncMap)
    def sharedDataAcceess = Mock(SharedDataAccess)
    def repository = new CandlestickAsyncRepositoryImpl(sharedDataAcceess)

    when:
    def resultFuture = repository.placeCollection(key, collection)
    waitComplete(resultFuture)

    then:
    noExceptionThrown()

    and:
    1 * sharedDataAcceess.getCandlestickCollectionMap() >> Future.succeededFuture(collectionAsyncMap)
    1 * collectionAsyncMap.put('ABC123.PT1S', collection) >> Future.succeededFuture()
  }

  def 'test amendCollection method'() {
    given:
    def initialCollection = [
      new Candlestick(amount: 10),
      new Candlestick(amount: 11)
    ]
    def amendedCollection = [
      new Candlestick(amount: 20),
      new Candlestick(amount: 21)
    ]

    def key = CandlestickCollectionKey.of(Isin.of('ABC123'), Length.of(Duration.parse('PT1S')))
    def mapkey = CandlestickAsyncRepositoryImpl.mapKeyString(key)

    def collectionAsyncMap = Mock(AsyncMap)
    def sharedDataAcceess = Mock(SharedDataAccess)
    def lock = Mock(Lock)

    def repository = new CandlestickAsyncRepositoryImpl(sharedDataAcceess)

    when:
    def resultFuture = repository.amendCollection(key, { col -> amendedCollection })
    waitComplete(resultFuture)

    then:
    noExceptionThrown()

    and:
    1 * sharedDataAcceess.accquireCandlestickCollectionWriteLock() >> Future.succeededFuture(lock)

    1 * sharedDataAcceess.getCandlestickCollectionMap() >> Future.succeededFuture(collectionAsyncMap)

    1 * collectionAsyncMap.get(mapkey) >> Future.succeededFuture(initialCollection)
    1 * collectionAsyncMap.put(mapkey, amendedCollection) >> Future.succeededFuture()

    1 * lock.release()
  }

  def 'test eraseCollection method'() {
    given:
    def collection = [
      new Candlestick(amount: 10),
      new Candlestick(amount: 11)
    ]

    def key = CandlestickCollectionKey.of(Isin.of('ABC123'), Length.of(Duration.parse('PT1S')))
    def mapkey = CandlestickAsyncRepositoryImpl.mapKeyString(key)

    def collectionAsyncMap = Mock(AsyncMap)
    def sharedDataAcceess = Mock(SharedDataAccess)
    def lock = Mock(Lock)

    def repository = new CandlestickAsyncRepositoryImpl(sharedDataAcceess)

    when:
    def erasedCollectionFuture = repository.eraseCollection(key)
    def erasedCollection = waitValue(erasedCollectionFuture)

    then:
    noExceptionThrown()

    and:
    1 * sharedDataAcceess.accquireCandlestickCollectionWriteLock() >> Future.succeededFuture(lock)

    1 * sharedDataAcceess.getCandlestickCollectionMap() >> Future.succeededFuture(collectionAsyncMap)

    1 * collectionAsyncMap.remove(mapkey) >> Future.succeededFuture(collection)

    1 * lock.release()
  }
}
