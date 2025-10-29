#pragma once

#include <string>
#include <vector>


class ITariff {
public:
    virtual float get_cost(float mass = 1) const = 0;

    virtual std::string get_name() const = 0;

    virtual ~ITariff() = default;
};

class Tariff : public ITariff {
protected:
    float rate;
    std::string name;
public:
    float get_cost(float mass = 1) const override {
        return mass * rate;
    }
    std::string get_name() const override {
        return name;
    }
    Tariff(float init_rate, std::string init_name) : rate(init_rate), name(init_name) {}
};

class DiscountTariff : public Tariff {
private:
    float discount_rate;
public:
    float get_cost(float mass = 1) const override {
        return mass * rate * (1 - discount_rate / 100.0);
    }
    DiscountTariff(float init_rate, std::string init_name, float init_discount_rate)
        : Tariff(init_rate, init_name), discount_rate(init_discount_rate) {}
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
    std::shared_ptr<ITariff> tariff;
    float mass;
    float total_cost;
public:
    OrderStatus status;

    void list_info() {
        std::cout << std::endl << "Order ID: " << id << std::endl << "Client: " << client.get_name() << std::endl
            << "Tariff: " << tariff->get_name() << std::endl << "Mass: " << mass << std::endl << "Total cost: " << total_cost << std::endl
            << "Status: " << static_cast<int>(status) << std::endl << std::endl;
    }
    float get_cost() { return total_cost; }
    Client get_client() { return client; }

    Order(unsigned init_id, Client init_client, std::shared_ptr<ITariff> init_tariff, float init_mass)
        : id(init_id), client(init_client), tariff(init_tariff), mass(init_mass)
    {
        status = OrderStatus::Created;
        total_cost = tariff->get_cost(mass);
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

    void add_tariff(const std::shared_ptr<ITariff>& element) {
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

    Client get_client(int num) {
        if (num >= clients.size()) {
            throw std::out_of_range("Index out of bounds");
        }
        return clients[num];
    }

    std::shared_ptr<ITariff> get_tariff(int num) {
        if (num >= tariffs.size()) {
            throw std::out_of_range("Index out of bounds");
        }
        return tariffs[num];
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
            std::cout << i+1 << ". " << tariffs[i]->get_name() << " — " << tariffs[i]->get_cost(1) << std::endl;
        }
        if (tariffs.size() == 0)
            std::cout << "Тарифов нет" << std::endl;
    }

    std::shared_ptr<ITariff> findCheapestTariff() const {
        if (tariffs.empty()) return nullptr;

        std::shared_ptr<ITariff> cheapest = tariffs[0];
        for (const auto& t : tariffs)
            if (t->get_cost() < cheapest->get_cost())
                cheapest = t;

        return cheapest;
    }

    float get_total_sum() {
        float sum = 0;
        for (Order order : data_) {
            sum += order.get_cost();
        }
        return sum;
    }

    float get_client_sum(Client target_client) {
        float sum = 0;
         for (Order order : data_)
            if (order.get_client().get_name() == target_client.get_name())
                sum += order.get_cost();
        return sum;
    }

    size_t size() { return size_; }
    size_t clients_count() { return clients.size(); }
    size_t tariffs_count() { return tariffs.size(); }

private:
    OrderMaster() {
        std::vector<Order> data_;
        std::vector<Client> clients;
        std::vector<std::shared_ptr<ITariff>> tariffs;
        size_t size = 0;
    }
    ~OrderMaster() {}

    OrderMaster(OrderMaster const&) = delete;
    OrderMaster& operator= (OrderMaster const&) = delete;

    std::vector<Order> data_;
    size_t size_;

    std::vector<Client> clients;
    std::vector<std::shared_ptr<ITariff>> tariffs;
};
