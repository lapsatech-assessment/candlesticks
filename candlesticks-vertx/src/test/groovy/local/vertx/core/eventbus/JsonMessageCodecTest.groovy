package local.vertx.core.eventbus

import io.vertx.core.buffer.Buffer
import spock.lang.Specification

class JsonMessageCodecTest extends Specification {

  def 'test decode/encode operations are mutually reversible'() {
    given:
    def codec = new JsonMessageCodec(SomeSerializable)
    def val = new SomeSerializable('text1')

    when:
    def buffer = Buffer.buffer()
    codec.encodeToWire(buffer, val)

    then:
    buffer.toString() == '{"text":"text1"}'
    
    when:
    def decodedVal = codec.decodeFromWire(0, buffer.copy())

    then:
    decodedVal instanceof SomeSerializable
    !decodedVal.is(val)
    decodedVal.text == val.text
  }

  def 'test systemCodecID equals to -1'() {
    given:
    def codec = new JsonMessageCodec(SomeSerializable)

    when:
    def id = codec.systemCodecID()

    then:
    id == -1
  }

  def 'test transform response with the same object'() {
    given:
    def codec = new JsonMessageCodec(SomeSerializable)
    def val = new SomeSerializable('text1')

    when:
    def transformedVal = codec.transform(val)

    then:
    transformedVal.is(val)
  }
}
