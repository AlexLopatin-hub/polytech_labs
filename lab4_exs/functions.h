#pragma once

#include <iostream>
#include <limits>
#include <conio.h>

#include "classes.h"


void print_menu();

int process_user_choice();

float get_positive_number(int upper_limit = 1e9, bool is_integer = false);

void get_client_sum();

int create_order();

void create_tariff();

void get_client_sum();

void find_cheapest_tariff();

void get_total_sum();