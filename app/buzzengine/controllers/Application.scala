package buzzengine.controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(buzzengine.views.html.index("Hello"))
  }
}
