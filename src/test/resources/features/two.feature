#language: ru
  Функционал: главный функционал
    Структура сценария: главный сценарий
      Если жму кнопку "<name>"
      Если выполняю шаблон "вход в систему" используя "первый функционал":
        | фамилия  |
        | Баринов  |
        | Сапёрова |
      То попадаю на страницу "<name>"
      Примеры:
      |name|
      |=======bugoga=======|
      Сценарий: супер сценарий
        Если выполняю сценарий "выход из системы" используя "первый функционал"
