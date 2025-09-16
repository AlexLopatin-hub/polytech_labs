#include <iostream>
#include <vector>

class Deal
{
private:
  int price;
  int weight;
  std::string company;
public:
  int gain() {
    return price * weight;
  }
  Deal(int init_price, int init_weight, std::string init_company)
      : price(init_price), weight(init_weight), company(init_company)
  {}
};

int main()
{
  std::vector<Deal> journal;

  Deal deal1(100, 150, "ООО Рога и Копыта");
  Deal deal2(400, 15, "North Bridge co");
  Deal deal3(1000, 3, "Lukoil");

  journal.push_back(deal1);
  journal.push_back(deal2);
  journal.push_back(deal3);

  int sum = 0;
  for (Deal i : journal)
    sum += i.gain();

  std::cout << sum << std::endl;
}
