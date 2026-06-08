package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;

public class HelpCommand implements ServerCommand {
    private static final String HELP = String.join("\n",
            "",
            "Инфо",
            "----",
            "  help                         показать эту справку",
            "  info                         тип, дата и размер коллекции",
            "  show                         вывести элементы коллекции",
            "",
            "Изменение коллекции",
            "-------------------",
            "  insert <key>                 добавить элемент по ключу",
            "  update <id>                  обновить элемент по id",
            "  remove_key <key>             удалить элемент по ключу",
            "  remove_lower                 удалить элементы меньше заданного",
            "  remove_greater_key <key>     удалить элементы с ключом больше key",
            "  remove_lower_key <key>       удалить элементы с ключом меньше key",
            "  clear                        очистить коллекцию",
            "",
            "Подсчет / группировка",
            "---------------------",
            "  group_counting_by_name       сгруппировать элементы по name",
            "  count_by_weapon_type [type]  посчитать элементы по weaponType",
            "  print_field_descending_mood  вывести mood по убыванию",
            "",
            "Клиентские команды",
            "------------------",
            "  execute_script <file>        выполнить команды из файла",
            "  exit                         завершить клиент"
    );

    @Override
    public Response execute(Request request) {
        return new Response(true, HELP);
    }
}
