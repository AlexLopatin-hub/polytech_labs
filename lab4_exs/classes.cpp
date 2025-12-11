#include "classes.h"

//Discount
float SeasonalDiscount::get_rate(float base_rate) const {
    return 1.0 - (discount_percent / 100.0) * is_applicable;
}

SeasonalDiscount::SeasonalDiscount(float discount_percent, bool is_applicable) :
    discount_percent(discount_percent), is_applicable(is_applicable) {}

float LoyaltyDiscount::get_rate(float base_rate) const {
    return base_rate * (1.0 - (std::min(client_months, 36) * 0.005));
}
void LoyaltyDiscount::increment_client_months() { client_months++; }

LoyaltyDiscount::LoyaltyDiscount(int client_months) : client_months(client_months) {}

float SeasonalLoyaltyDiscount::get_rate(float base_rate) const {
    return base_rate * (1.0 - (std::min(client_months, 36) * 0.005)
        - (discount_percent / 100.0) * is_applicable);
}

SeasonalLoyaltyDiscount::SeasonalLoyaltyDiscount(float discount_percent, bool is_applicable, int client_months) :
    SeasonalDiscount{ 0, is_applicable }, LoyaltyDiscount{ client_months }, discount_percent(discount_percent) {}

// Tariff
float Tariff::get_cost(float mass) {
    if (!discount) {
        return mass * base_rate;
    }
    return mass * discount->get_rate(base_rate);
}

std::string Tariff::get_name() {
    return name;
}

Tariff::Tariff(float rate, std::string name, float discount, int months) : base_rate(rate), name(name) {
    this->discount = new SeasonalLoyaltyDiscount(discount, 1, months);
}

// Client
unsigned Client::get_id() {
    return id;
}

std::string Client::get_name() {
    return name;
}

Client::Client(unsigned id, std::string name) : id(id), name(name) {}

// Order
void Order::list_info() {
    std::cout << std::endl << "Order ID: " << id << std::endl << "Client: " << client.get_name() << std::endl
        << "Tariff: " << tariff.get_name() << std::endl << "Mass: " << mass << std::endl << "Total cost: " << total_cost << std::endl
        << "Status: " << static_cast<int>(status) << std::endl << std::endl;
}

float Order::get_cost() { return total_cost; }

Client Order::get_client() { return client; }

Order::Order(unsigned id, Client client, Tariff tariff, float mass)
    : id(id), client(client), tariff(tariff), mass(mass)
{
    status = OrderStatus::Created;
    total_cost = tariff.get_cost(mass);
}

Order::~Order() {}


// OrderMaster
OrderMaster& OrderMaster::Instance() {
    static OrderMaster instance;
    return instance;
}

void OrderMaster::add_tariff(const Tariff& element) {
    tariffs.push_back(element);
}

void OrderMaster::add_client(const Client& element) {
    clients.push_back(element);
}

void OrderMaster::push_back(const Order& element) {
    data_.push_back(element);
    size_++;
}

Order& OrderMaster::operator[](size_t index) {
    if (index >= size_)
        throw std::out_of_range("Index out of bounds");
    return data_[index];
}

Client OrderMaster::get_client(int num) {
    if (num >= clients.size()) {
        throw std::out_of_range("Index out of bounds");
    }
    return clients[num];
}

Tariff OrderMaster::get_tariff(int num) {
    if (num >= tariffs.size()) {
        throw std::out_of_range("Index out of bounds");
    }
    return tariffs[num];
}

void OrderMaster::list_orders() {
    if (size_ > 0)
        for (Order order : data_)
            order.list_info();
    else
        std::cout << "Заказов в данный момент нет" << std::endl;
}

void OrderMaster::list_clients() {
    for (int i = 0; i < clients.size(); i++) {
        std::cout << i + 1 << ". " << clients[i].get_name() << std::endl;
    }
    if (clients.size() == 0)
        std::cout << "Клиенотв нет" << std::endl;
}

void OrderMaster::list_tariffs() {
    for (int i = 0; i < tariffs.size(); i++) {
        std::cout << i + 1 << ". " << tariffs[i].get_name() << " — " << tariffs[i].get_cost(1) << std::endl;
    }
    if (tariffs.size() == 0)
        std::cout << "Тарифов нет" << std::endl;
}

float OrderMaster::get_total_sum() {
    float sum = 0;
    for (Order order : data_) {
        sum += order.get_cost();
    }
    return sum;
}

Tariff* OrderMaster::findCheapestTariff() {
    if (tariffs.empty()) {
        std::cout << "Тарифов нет." << std::endl;
        return nullptr;
    }

    Tariff* cheapest = &tariffs[0];
    for (auto& t : tariffs)
        if (t.get_cost() < cheapest->get_cost())
            cheapest = &t;
        
    return cheapest;
}

float OrderMaster::get_client_sum(Client target_client) {
    float sum = 0;
    for (Order order : data_)
        if (order.get_client().get_name() == target_client.get_name())
            sum += order.get_cost();
    return sum;
}

void OrderMaster::create_client() {
    std::string client_name;
    std::cout << "Введите имя клиента: ";
    std::cin >> client_name;

    Client client(0, client_name);
    add_client(client);
    return;
}

size_t OrderMaster::size() { return size_; }
size_t OrderMaster::clients_count() { return clients.size(); }
size_t OrderMaster::tariffs_count() { return tariffs.size(); }


