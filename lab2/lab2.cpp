#include <iostream>
#include <limits>
#include <cmath>
#include "lab2.h"
#include "tariffs&clients.h"

OrderMaster& master = OrderMaster::Instance();
unsigned order_id_counter = 0;

void print_menu() {
    std::cout << "Панель управления заказами" << std::endl << "1. Создать заказ" << std::endl <<
        "2. Список заказов" << std::endl << "3. Список тарифов" << std::endl << "4. Список клиентов" <<
        std::endl << "5. Добавить клиента" << std::endl << std::endl << "0. Выйти" << std::endl << std::endl;
}

void list_clients() {
    for (int i = 0; i < clients.size(); i++) {
        std::cout << i+1 << ". " << clients[i].get_name() << std::endl;
    }
}

void list_tariffs() {
    for (int i = 0; i < tariffs.size(); i++) {
        std::cout << i+1 << ". " << tariffs[i].get_name() << std::endl;
    }
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

unsigned create_order() {
    std::cout << "Выберите клиента:" << std::endl;
    list_clients();
    int choice = get_positive_number(clients.size(), true);
    Client client = clients[choice - 1];

    std::cout << "Выюерите тариф" << std::endl;
    list_tariffs();
    choice = get_positive_number(tariffs.size(), true);
    Tariff tariff = tariffs[choice - 1];

    std::cout << "Введите массу перевозимого груза: ";
    float mass = get_positive_number();

    unsigned id = order_id_counter++;

    Order order(id, client, tariff, mass);
    master.push_back(order);
    return id;
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
