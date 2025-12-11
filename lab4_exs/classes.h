#pragma once

#include <string>
#include <vector>
#include <iostream>
#include "functions.h"


class IDiscount {
public:
    virtual float get_rate(float base_rate) const = 0;

    virtual ~IDiscount() = default;
};

class SeasonalDiscount : virtual public IDiscount{
private:
    float discount_percent;
public:
    bool is_applicable;

    float get_rate(float base_rate) const override;

    SeasonalDiscount(float discount_percent, bool is_applicable);
};

class LoyaltyDiscount : virtual public IDiscount{
protected:
    int client_months;
public:
    float get_rate(float base_rate) const override;

    void increment_client_months();

    LoyaltyDiscount(int client_months);
};

class SeasonalLoyaltyDiscount : public SeasonalDiscount, public LoyaltyDiscount {
private:
    float discount_percent;
public:
    float get_rate(float base_rate) const override;
    
    SeasonalLoyaltyDiscount(float discount_percent, bool is_applicable, int client_months);
};

class Tariff {
private:
    float base_rate;
    std::string name;
    IDiscount* discount;
public:
    float get_cost(float mass = 1);

    std::string get_name();

    Tariff(float rate, std::string name, float discount, int months);
};

class Client {
private:
    unsigned id;
    std::string name;
public:
    unsigned get_id();

    std::string get_name();

    Client(unsigned id, std::string name);
};

enum class OrderStatus { Created, InTransit, Delivered, Cancelled };

class Order {
private:
    unsigned id;
    Client client;
    Tariff tariff;
    float mass;
    float total_cost;
public:
    OrderStatus status;

    void list_info();
    float get_cost();
    Client get_client();

    Order(unsigned id, Client client, Tariff tariff, float mass);
    ~Order();
};

class OrderMaster {
private:
    OrderMaster() {
        std::vector<Order> data_;
        std::vector<Client> clients;
        std::vector<Tariff> tariffs;
        size_t size = 0;
        unsigned order_id_counter = 1;
    }
    ~OrderMaster() {}

    OrderMaster(OrderMaster const&) = delete;
    OrderMaster& operator= (OrderMaster const&) = delete;
    
    std::vector<Order> data_;
    size_t size_;

    std::vector<Client> clients;
    std::vector<Tariff> tariffs;

public:
    static OrderMaster& Instance();

    unsigned order_id_counter;

    void add_tariff(const Tariff& element);

    void add_client(const Client& element);

    void push_back(const Order& element);

    Order& operator[](size_t index);

    Client get_client(int num);

    Tariff get_tariff(int num);

    void list_orders();

    void list_clients();

    void list_tariffs();

    float get_total_sum();

    Tariff* findCheapestTariff();

    float get_client_sum(Client target_client);

    void create_client();

    size_t size();
    size_t clients_count();
    size_t tariffs_count();
};