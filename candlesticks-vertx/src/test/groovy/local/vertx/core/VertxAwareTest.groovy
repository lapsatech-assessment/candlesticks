package local.vertx.core

import io.vertx.core.Promise
import io.vertx.core.Vertx
import utils.VertxAwareSpecification
import utils.VertxTestUtils

class VertxAwareTest extends VertxAwareSpecification {

  def 'An attempt to get Vertx object out of the Vertx context should fail'() {
    given:
    def test = new TestVertxAware()

    when:
    test.vertx()

    then:
    thrown(IllegalStateException)
  }

  def 'Vertx object should be resolved when called inside Vertx context'() {
    given:
    def test = new TestVertxAware()
    def p = Promise.<Vertx>promise()

    when:
    vertx.runOnContext { p.complete(test.vertx()) }
    def vertxResolved = VertxTestUtils.waitValue(p)

    then:
    vertxResolved == vertx

    and:
    noExceptionThrown()
  }

  def 'Vertx object should be resolved out of the Vertx context when passed to the ctor explicitly'() {
    given:
    def test = new TestVertxAware(vertx)

    when:
    def vertxResolved = test.vertx()

    then:
    vertxResolved == vertx

    and:
    noExceptionThrown()
  }
}

class TestVertxAware extends VertxAware {

  public TestVertxAware() {}

  public TestVertxAware(Vertx vertx) {
    super(vertx)
  }
}
