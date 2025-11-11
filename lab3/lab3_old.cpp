#include <iostream>
#include <limits>
#include <cmath>
#include <memory>
#include "lab3.h"

OrderMaster& master = OrderMaster::Instance();
unsigned order_id_counter = 0;

void print_menu() {
    std::cout << "Панель управления заказами" << std::endl <<
        "1. Создать заказ" << std::endl <<
        "2. Список заказов" << std::endl <<
        "3. Список тарифов" << std::endl <<
        "4. Список клиентов" << std::endl <<
        "5. Добавить клиента" << std::endl <<
        "6. Добавить тариф"<< std::endl <<
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

float get_positive_number(int upper_limit = 1e9, bool is_integer = false) {
    float num;
    while (true) {
        std::cin >> num;
        if (!std::cin) {
            std::cout << "Ввведите число" << std::endl;
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            continue;
        }
        else if (num <= 0) {
            std::cout << "Чилсо должно быть неотрицательным" << std::endl;
            continue;
        }
        else if (num > upper_limit) {
            std::cout << "Чилсо должно быть не больше, чем " << upper_limit << std::endl;
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

void create_client() {
    std::string client_name;
    std::cout << "Введите имя клиента: ";
    std::cin >> client_name;

    Client client(0, client_name);
    master.add_client(client);
    return;
}

void create_tariff() {
    std::string tariff_name;
    std::cout << "Введите название тарифа: ";
    std::cin >> tariff_name;

    float rate;
    std::cout << "Ввдеите ставку тарифа (за кг): ";
    rate = get_positive_number();

    std::unique_ptr tariff { new Tariff(rate, tariff_name) };
    master.add_tariff(tariff);
    return;
}

unsigned create_order() {
    std::cout << "Выберите клиента:" << std::endl;
    master.list_clients();
    int choice = get_positive_number(master.clients_count(), true);
    Client client = master.get_client(choice - 1);

    std::cout << "Выюерите тариф" << std::endl;
    master.list_tariffs();
    choice = get_positive_number(master.tariffs_count(), true);
    std::unique_ptr<ITariff> tariff = master.get_tariff(choice - 1);

    std::cout << "Введите массу перевозимого груза: ";
    float mass = get_positive_number();

    unsigned id = order_id_counter++;

    Order order(id, client, tariff, mass);
    master.push_back(order);
    return id;
}

void get_client_sum() {
    std::cout << "Выберите клиента:" << std::endl;
    master.list_clients();
    if (master.clients_count() == 0) { return; }
    int choice = get_positive_number(master.clients_count(), true);
    Client client = master.get_client(choice - 1);
    float sum = 0;
    sum = master.get_client_sum(client);
    std::cout << "Сумма заказов для клиента " << client.get_name() << " равна " << sum << std::endl << std::endl;
}

void get_total_sum() {
    float sum = master.get_total_sum();
    std::cout << "Общая сумма всех заказов равна " << sum << std::endl << std::endl;
}

void find_cheapest_tariff() {
    std::unique_ptr<ITariff> cheapest = master.findCheapestTariff();
    if (cheapest) {
        std::cout << "Самый дешёвый тариф: " << cheapest->get_name()
            << " по ставке " << cheapest->get_cost() << " за кг." << std::endl;
    } else {
        std::cout << "Тарифов нет." << std::endl;
    }
}

int main()
{
    system("clear");
    while (true) {
        unsigned order_id = 0;
        bool wrong_input = false;
        print_menu();

        int choice = process_user_choice();
        switch (choice) {
            case 0:
                break;
            case 1:
                system("clear");
                order_id = create_order();
                system("clear");
                std::cout << "Заказ создан. ID заказа: " << order_id << std::endl;
                break;
            case 2:
                system("clear");
                master.list_orders();
                std::cout << "Нажмите Enter чтобы вернуться";
                process_user_choice();
                system("clear");
                break;
            case 3:
                system("clear");
                master.list_tariffs();
                std::cout << "Нажмите Enter чтобы вернуться";
                process_user_choice();
                system("clear");
                break;
            case 4:
                system("clear");
                master.list_clients();
                std::cout << "Нажмите Enter чтобы вернуться";
                process_user_choice();
                system("clear");
                break;
            case 5:
                system("clear");
                create_client();
                system("clear");
                std::cout << "Клиент добавлен" << std::endl;
                break;
            case 6:
                system("clear");
                create_tariff();
                system("clear");
                std::cout << "Тариф добавлен" << std::endl;
                break;
            case 7:
                system("clear");
                get_client_sum();
                std::cout << "Нажмите Enter чтобы вернуться";
                process_user_choice();
                system("clear");
                break;
            case 8:
                system("clear");
                get_total_sum();
                std::cout << "Нажмите Enter чтобы вернуться";
                process_user_choice();
                system("clear");
                break;
            case 9:
                system("clear");
                find_cheapest_tariff();
                std::cout << "Нажмите Enter чтобы вернуться";
                process_user_choice();
                system("clear");
                break;
            default:
                wrong_input = true;
                system("clear");
        }
        if (wrong_input)
            std::cout << "Неверный ввод" << std::endl;
        if (choice == 0) break;
    }
    return 0;
}
