#include "functions.h"

OrderMaster& masterr = OrderMaster::Instance();

void print_menu() {
    std::cout << "Панель управления заказами" << std::endl <<
        "1. Создать заказ" << std::endl <<
        "2. Список заказов" << std::endl <<
        "3. Список тарифов" << std::endl <<
        "4. Список клиентов" << std::endl <<
        "5. Добавить клиента" << std::endl <<
        "6. Добавить тариф" << std::endl <<
        "7. Вывести сумму заказов для клиента" << std::endl <<
        "8. Вывести сумму всех заказов" << std::endl <<
        "9. Найти самый дешёвый тариф " << std::endl << std::endl <<
        "0. Выйти" << std::endl << std::endl;
}

int process_user_choice() {
    int choice = 0;
    std::cin >> choice;
    if (!std::cin) {
        choice = 999;
        std::cin.clear();
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    }
    return choice;
}

float get_positive_number(int upper_limit, bool is_integer) {
    float num;
    while (true) {
        std::cin >> num;
        if (!std::cin) {
            std::cout << "Ввведите число" << std::endl;
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            continue;
        }
        else if (num < 0) {
            std::cout << "Число должно быть неотрицательным" << std::endl;
            continue;
        }
        else if (num > upper_limit) {
            std::cout << "Число должно быть не больше, чем " << upper_limit << std::endl;
            continue;
        }
        else if (is_integer && num != round(num)) {
            std::cout << "Число должно быть целым" << std::endl;
            continue;
        }
        break;
    }
    return num;
}

int create_order() {
    if (masterr.clients_count() == 0) {
        std::cout << "Нет клиентов. Нажмите Enter чтобы вернуться" << std::endl;
        _getch();
        return -1;
    }
    if (masterr.tariffs_count() == 0) {
        std::cout << "Нет тарифов. Нажмите Enter чтобы вернуться" << std::endl;
        _getch();
        return -1;
    }

    std::cout << "Выберите клиента:" << std::endl;
    masterr.list_clients();
    int choice = get_positive_number(masterr.clients_count(), true);
    Client client = masterr.get_client(choice - 1);

    std::cout << "Выберите тариф" << std::endl;
    masterr.list_tariffs();
    choice = get_positive_number(masterr.tariffs_count(), true);
    Tariff tariff = masterr.get_tariff(choice - 1);

    std::cout << "Введите массу перевозимого груза: ";
    float mass = get_positive_number();

    int id = masterr.order_id_counter++;

    Order order(id, client, tariff, mass);
    masterr.push_back(order);
    return id;
}

void create_tariff() {
    std::string tariff_name;
    std::cout << "Введите название тарифа: ";
    std::cin >> tariff_name;

    float rate;
    std::cout << "Ввдеите ставку тарифа (за кг): ";
    rate = get_positive_number();

    float discount;
    std::cout << "Ввдеите скидку, предоставляемую на тариф: ";
    discount = get_positive_number(100);

    int months;
    std::cout << "Ввдеите число месяцев, в течение которых клиент использует наши услуги "
              << "(для предоставления скидки по программе лояльности): ";
    months = get_positive_number();

    Tariff tariff(rate, tariff_name, discount, months);
    masterr.add_tariff(tariff);
    return;
}

void get_client_sum() {
    std::cout << "Выберите клиента:" << std::endl;
    masterr.list_clients();
    if (masterr.clients_count() == 0) { return; }
    int choice = get_positive_number(masterr.clients_count(), true);
    Client client = masterr.get_client(choice - 1);
    float sum = 0;
    sum = masterr.get_client_sum(client);
    std::cout << "Сумма заказов для клиента " << client.get_name() << " равна " << sum << std::endl << std::endl;
}

void find_cheapest_tariff() {
    Tariff* cheapest = masterr.findCheapestTariff();
    if (cheapest) {
        std::cout << "Самый дешёвый тариф: " << cheapest->get_name()
            << " по ставке " << cheapest->get_cost() << " за кг." << std::endl;
    }
    else {
        std::cout << "Тарифов нет." << std::endl;
    }
}

void get_total_sum() {
    float sum = masterr.get_total_sum();
    std::cout << "Общая сумма всех заказов равна " << sum << std::endl << std::endl;
}