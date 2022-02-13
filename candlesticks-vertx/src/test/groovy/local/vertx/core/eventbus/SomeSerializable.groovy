package local.vertx.core.eventbus

class SomeSerializable implements Serializable {

  public static final long serialVersionUID = 42L

  public SomeSerializable() {
  }

  public SomeSerializable(String text) {
    this.text = text;
  }

  public String text;
}