#pragma once

#include <string>
#include <vector>

class Tariff {
private:
    float rate;
    std::string name;
public:
    float get_cost(float mass) {
        return mass * rate;
    }
    std::string get_name() {
        return name;
    }
    Tariff(float init_rate, std::string init_name) : rate(init_rate), name(init_name) {}
};

class Client {
private:
    unsigned id;
    std::string name;
public:
    unsigned get_id() {
        return id;
    }
    std::string get_name() {
        return name;
    }

    Client(unsigned init_id, std::string init_name) : id(init_id), name(init_name) {}
};

enum class OrderStatus {Created, InTransit, Delivered, Cancelled};

class Order {
private:
    unsigned id;
    Client client;
    Tariff tariff;
    float mass;
    float total_cost;
public:
    OrderStatus status;

    void list_info() {
        std::cout << std::endl << "Order ID: " << id << std::endl << "Client: " << client.get_name() << std::endl
            << "Tariff: " << tariff.get_name() << std::endl << "Mass: " << mass << std::endl << "Total cost: " << total_cost << std::endl
            << "Status: " << static_cast<int>(status) << std::endl << std::endl;
    }
    float get_cost() { return total_cost; }

    Order(unsigned init_id, Client init_client, Tariff init_tariff, float init_mass)
        : id(init_id), client(init_client), tariff(init_tariff), mass(init_mass)
    {
        status = OrderStatus::Created;
        total_cost = tariff.get_cost(mass);
    }
    ~Order() {
        std::cout << "log: order " << id << " deleted" << std::endl;
    }
};

class OrderMaster {
public:
    static OrderMaster& Instance() {
        static OrderMaster instance;
        return instance;
    }

    void add_tariff(const Tariff& element) {
        tariffs.push_back(element);
    }

    void add_client(const Client& element) {
        clients.push_back(element);
    }

    void push_back(const Order& element) {
        data_.push_back(element);
        size_++;
    }

    Order& operator[](size_t index) {
        if (index >= size_)
            throw std::out_of_range("Index out of bounds");
        return data_[index];
    }

    void list_orders() {
        if (size_ > 0)
            for (Order order : data_)
                order.list_info();
        else
            std::cout << "Заказов в данный момент нет" << std::endl;
    }

    void list_clients() {
        for (int i = 0; i < clients.size(); i++) {
            std::cout << i+1 << ". " << clients[i].get_name() << std::endl;
        }
        if (clients.size() == 0)
            std::cout << "Клиенотв нет" << std::endl;
    }

    void list_tariffs() {
        for (int i = 0; i < tariffs.size(); i++) {
            std::cout << i+1 << ". " << tariffs[i].get_name() << " — " << tariffs[i].get_cost(1) << std::endl;
        }
        if (tariffs.size() == 0)
            std::cout << "Тарифов нет" << std::endl;
    }

    float get_total_sum() {
        float sum = 0;
        for (Order order : data_) {
            sum += order.get_cost();
        }
        return sum;
    }

    size_t size() { return size_; }

private:
    OrderMaster() {
        std::vector<Order> data_;
        std::vector<Client> clients;
        std::vector<Tariff> tariffs;
        size_t size = 0;
    }
    ~OrderMaster() {}

    OrderMaster(OrderMaster const&) = delete;
    OrderMaster& operator= (OrderMaster const&) = delete;

    std::vector<Order> data_;
    size_t size_;

    std::vector<Client> clients;
    std::vector<Tariff> tariffs;
};
