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


Tariff findCheapestTariff() const {
    if (tariffs.empty()) return nullptr;

    std::unique_ptr<ITariff> cheapest = tariffs[0];
    for (const auto& t : tariffs)
        if (t->get_cost() < cheapest->get_cost())
            cheapest = t;

    return cheapest;
}

void find_cheapest_tariff() {
    Tariff cheapest = master.findCheapestTariff();
    if (cheapest) {
        std::cout << "Самый дешёвый тариф: " << cheapest->get_name()
        << " по ставке " << cheapest->get_cost() << " за кг." << std::endl;
    } else {
        std::cout << "Тарифов нет." << std::endl;
    }
}
