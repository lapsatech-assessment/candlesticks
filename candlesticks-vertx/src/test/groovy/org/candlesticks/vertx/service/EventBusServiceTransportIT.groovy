package org.candlesticks.vertx.service

import static utils.VertxTestUtils.waitComplete
import static utils.VertxTestUtils.waitValue

import org.candlesticks.vertx.service.EventBusServiceTransport

import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Promise
import local.vertx.core.AsyncService
import utils.VertxAwareSpecification
import utils.VertxTestUtils

class EventBusServiceTransportIT extends VertxAwareSpecification {

  def 'register service and make a call via vertx eventbus'() {
    given:
    def asyncService = Mock(AsyncService)
    def transport = EventBusServiceTransport.instance()

    when:
    def registered = Promise.<Void>promise()
    vertx.runOnContext{
      transport.registerService('test1', SomeInput.class, asyncService)
          .onComplete(registered)
    }
    waitComplete(registered)

    then:
    noExceptionThrown()

    when:
    def responseReceived = Promise.<SomeOutput>promise()
    vertx.runOnContext{
      transport.callService('test1', new SomeInput(text: 'input1'), SomeOutput.class)
      .onComplete(responseReceived)
    }
    def response = waitValue(responseReceived)

    then:
    1 * asyncService.callAsync(new SomeInput(text: 'input1')) >> Future.succeededFuture(new SomeOutput(text: 'output1'))

    and:
    response == new SomeOutput(text: 'output1')
  }

  def 'register stub and publish via vertx eventbus'() {
    given:
    def transport = EventBusServiceTransport.instance()
    def eventReceived = Promise.<SomeInput>promise()

    when:
    def registered = Promise.promise()
    vertx.runOnContext{
      transport.registerStub('test2', SomeInput.class, { event -> eventReceived.complete(event)  })
      .onComplete(registered)
    }
    waitComplete(registered)

    then:
    noExceptionThrown()

    when:
    def published = Promise.<Void>promise()
    vertx.runOnContext{
      transport.publish('test2', new SomeInput(text: 'input1'))
      .onComplete(published)
    }
    waitComplete(published)
    def event = waitValue(eventReceived)

    then:
    event == new SomeInput(text: 'input1')
  }
}



class SomeInput implements Serializable {

  private String text

  public SomeInput() {
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this.is(obj))
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SomeInput other = (SomeInput) obj;
    return Objects.equals(text, other.text);
  }
}

class SomeOutput implements Serializable {

  private String text

  public SomeOutput() {
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this.is(obj))
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SomeOutput other = (SomeOutput) obj;
    return Objects.equals(text, other.text);
  }
}