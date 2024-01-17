package SpaceData.view

object TUIStrings {

  def printHeader() : Unit = {
    println(
    """
                                                               
      |||=================================================================================================================||
      |||                      ___________  ___  _____  _____           ______  ___ _____ ___                             ||
      |||                     /  ___| ___ \/ _ \/  __ \|  ___|          |  _  \/ _ \_   _/ _ \                            ||
      |||                     \ `--.| |_/ / /_\ \ /  \/| |__    ______  | | | / /_\ \| |/ /_\ \                           ||   
      |||                      `--. \  __/|  _  | |    |  __|  |______| | | | |  _  || ||  _  |                           ||
      |||                     /\__/ / |   | | | | \__/\| |___           | |/ /| | | || || | | |                           ||
      |||                     \____/\_|   \_| |_/\____/\____/           |___/ \_| |_/\_/\_| |_/                           ||
      |||                                                                                                                 ||
      |||                                                                                                                 ||
      |||                                     Press "d" to show the Dashboard                                             ||
      |||                                     Press "sl" to show Starlink Satalites                                       ||
      |||                                     Press "la" to show Launches                                                 ||
      |||                                     Press "r" to show Rockets                                                   ||
      |||                                     Press "rid" to show Rockets                                                 ||
      |||                                     Press "slid" to show specific Starlink Satalite details via the id          ||
      |||                                     Press "laid" to show specific Launch details via the id                     ||
      |||                                     Press "slspeed" to calculate Starlink Sat speed                             ||
      |||                                     Type "dsl" to enter DSL mode                                                ||
      |||                                     Type "dslfile" to enter DSL-File mode                                       ||
      |||                                     Type "exit" to exit the tool                                                ||
      |||                                                                                                                 ||
      |||=================================================================================================================||
      |""".stripMargin
    )
  }

  def printStarlink(): Unit = {
    println(
    """
       _____ _____ ___  ______ _     _____ _   _  _   __
      /  ___|_   _/ _ \ | ___ \ |   |_   _| \ | || | / /
      \ `--.  | |/ /_\ \| |_/ / |     | | |  \| || |/ / 
       `--. \ | ||  _  ||    /| |     | | | . ` ||    \ 
      /\__/ / | || | | || |\ \| |_____| |_| |\  || |\  \
      \____/  \_/\_| |_/\_| \_\_____/\___/\_| \_/\_| \_/
      """.stripMargin
    )
  }

  def printRockets(): Unit = {
    println(
      """
    |  ______ _____ _____  _   __ _____ _____ _____ 
    |  | ___ \  _  /  __ \| | / /|  ___|_   _/  ___|
    |  | |_/ / | | | /  \/| |/ / | |__   | | \ `--. 
    |  |    /| | | | |    |    \ |  __|  | |  `--. \
    |  | |\ \\ \_/ / \__/\| |\  \| |___  | | /\__/ /
    |  \_| \_|\___/ \____/\_| \_/\____/  \_/ \____/                              
    """.stripMargin
    )
  }

  def printDashboardFirstRow(): Unit = {
    println(
    """
      |║ Category, Numbers                                             
      |║+---------------------+
      |""".stripMargin
    )
  }

  def printLaunches(): Unit = {
    println(
    """
      |║ ██       █████  ██    ██ ███    ██  ██████ ██   ██ ███████ ███████ 
      |║ ██      ██   ██ ██    ██ ████   ██ ██      ██   ██ ██      ██      
      |║ ██      ███████ ██    ██ ██ ██  ██ ██      ███████ █████   ███████ 
      |║ ██      ██   ██ ██    ██ ██  ██ ██ ██      ██   ██ ██           ██ 
      |║ ███████ ██   ██  ██████  ██   ████  ██████ ██   ██ ███████ ███████ 
      |║ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      |""".stripMargin
      )
  }

  def printDetails(): Unit = {
    println(
    """
      |║ ██████  ███████ ████████  █████  ██ ██      ███████ 
      |║ ██   ██ ██         ██    ██   ██ ██ ██      ██      
      |║ ██   ██ █████      ██    ███████ ██ ██      ███████ 
      |║ ██   ██ ██         ██    ██   ██ ██ ██           ██ 
      |║ ██████  ███████    ██    ██   ██ ██ ███████ ███████ 
      |║ - - - - - - - - - - - - - - - - - - - - - - - - - - -
      |""".stripMargin
    )
  }

  def printHelpLine(): Unit = {
    println(
    """Finished: press enter to show menü..."""
    )
  }

}


