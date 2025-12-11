#include <iostream>
#include <cmath>
#include <conio.h>
#include "classes.h"
#include "functions.h"

OrderMaster& master = OrderMaster::Instance();

int main()
{
    setlocale(LC_ALL, "RU");
    int order_id = 0;
    system("cls");
    while (true) {
        bool wrong_input = false;
        print_menu();
        int choice = process_user_choice();
        switch (choice) {
        case 0:
            break;
        case 1:
            system("cls");
            order_id = create_order();
            system("cls");
            std::cout << "Заказ создан. ID заказа: " << order_id << std::endl;
            break;
        case 2:
            system("cls");
            master.list_orders();
            std::cout << "Нажмите Enter чтобы вернуться";
            _getch();
            system("cls");
            break;
        case 3:
            system("cls");
            master.list_tariffs();
            std::cout << "Нажмите Enter чтобы вернуться";
            _getch();
            system("cls");
            break;
        case 4:
            system("cls");
            master.list_clients();
            std::cout << "Нажмите Enter чтобы вернуться";
            _getch();
            system("cls");
            break;
        case 5:
            system("cls");
            master.create_client();
            system("cls");
            std::cout << "Клиент добавлен" << std::endl;
            break;
        case 6:
            system("cls");
            create_tariff();
            system("cls");
            std::cout << "Тариф добавлен" << std::endl;
            break;
        case 7:
            system("cls");
            get_client_sum();
            std::cout << "Нажмите Enter чтобы вернуться";
            _getch();
            system("cls");
            break;
        case 8:
            system("cls");
            get_total_sum();
            std::cout << "Нажмите Enter чтобы вернуться";
            _getch();
            system("cls");
            break;
        case 9:
            system("cls");
            find_cheapest_tariff();
            std::cout << "Нажмите Enter чтобы вернуться";
            _getch();
            system("cls");
            break;
        default:
            wrong_input = true;
            system("cls");
        }
        if (wrong_input)
            std::cout << "Неверный ввод" << std::endl;
        if (choice == 0) break;
    }
    return 0;
}