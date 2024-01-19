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
      |||                     Type "d" to show the Dashboard                                                              ||
      |||                     Type "sl" to show Starlink Satalites                                                        ||
      |||                     Type "la" to show Launches                                                                  ||
      |||                     Type "r" to show Rockets                                                                    ||
      |||                     Type "rid" to show Rockets                                                                  ||
      |||                     Type "slid" to show specific Starlink Satalite details via the id                           ||
      |||                     Type "laid" to show specific Launch details via the id                                      ||
      |||                     Type "slspeed" to calculate Starlink Sat speed                                              ||
      |||                     Type "dsl" to enter DSL mode                                                                ||
      |||                     Type "dslfile" to enter DSL-File mode                                                       ||
      |||                     Type "exit" to exit the tool                                                                ||
      |||                                                                                                                 ||
      |||=================================================================================================================||
      |""".stripMargin
    )
  }

  def printStarlink(): Unit = {
    println(
    """
    |   _____ _____ ___  ______ _     _____ _   _  _   __
    |  /  ___|_   _/ _ \ | ___ \ |   |_   _| \ | || | / /
    |  \ `--.  | |/ /_\ \| |_/ / |     | | |  \| || |/ / 
    |   `--. \ | ||  _  ||    /| |     | | | . ` ||    \ 
    |  /\__/ / | || | | || |\ \| |_____| |_| |\  || |\  \
    |  \____/  \_/\_| |_/\_| \_\_____/\___/\_| \_/\_| \_/
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
      |Category, Numbers                                             
      |+---------------------+
      |""".stripMargin
    )
  }

  def printDetails(): Unit = {
    println(
    """
    |  ______ _____ _____ ___  _____ _      _____ 
    |  |  _  \  ___|_   _/ _ \|_   _| |    /  ___|
    |  | | | | |__   | |/ /_\ \ | | | |    \ `--. 
    |  | | | |  __|  | ||  _  | | | | |     `--. \
    |  | |/ /| |___  | || | | |_| |_| |____/\__/ /
    |  |___/ \____/  \_/\_| |_/\___/\_____/\____/                                            
    """.stripMargin
    )
  }

  def printHelpLine(): Unit = {
    println(
    """Finished: press enter to show menu..."""
    )
  }

}


