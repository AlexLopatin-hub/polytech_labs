#include <iostream>
#include <vector>

class Deal
{
private:
  unsigned price;
  unsigned weight;
  std::string company;
public:
  unsigned gain() {
    return price * weight;
  }
  Deal(unsigned  init_price, unsigned init_weight, std::string init_company)
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

  unsigned sum = 0;
  for (Deal i : journal)
    sum += i.gain();

  std::cout << sum << std::endl;

  return 0;
}
