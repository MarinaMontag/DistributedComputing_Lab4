package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

var queueOfCitiesToAdd []*City

func createCity(value string) *City {
	return &City{value, false}
}

func createQueueOfCities() {
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("A"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("B"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("C"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("D"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("E"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("G"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("H"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("I"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("J"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("K"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("L"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("M"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("N"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("O"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("P"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("Q"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("Y"))
	queueOfCitiesToAdd = append(queueOfCitiesToAdd, createCity("Z"))

}

func main() {
	airport := createAirport()
	createQueueOfCities()
	airport.wg.Add(4)
	go AddDeleteCities(airport)
	go AddDeleteFlights(airport)
	go ChangePrice(airport)
	go SearchRouteFromAtoB(airport)
	airport.wg.Wait()
}

type City struct {
	name    string
	visited bool
}

type Flight struct {
	price     int
	direction *City
}

type Airport struct {
	wg      *sync.WaitGroup
	cities  []*City
	flights map[*City][]*Flight
	lock    *sync.RWMutex
}

func createAirport() *Airport {
	var rwLock sync.RWMutex
	var wg sync.WaitGroup
	return &Airport{&wg, nil, make(map[*City][]*Flight), &rwLock}
}

func (graph *Airport) display() {
	for _, city := range graph.cities {
		fmt.Print(city.name + "->")
		if len(graph.flights[city]) == 0 {
			fmt.Println()
		}
		for j, flight := range graph.flights[city] {
			fmt.Printf("%v {%v}", flight.direction.name, flight.price)
			if j == len(graph.flights[city])-1 {
				fmt.Println()
			} else {
				fmt.Print(", ")
			}
		}
	}
	fmt.Println()
}

//потік, що змінює ціну квитка;
func ChangePrice(graph *Airport) {
	defer graph.wg.Done()
	for {
		graph.lock.Lock()
		if len(queueOfCitiesToAdd) > 0 {
			if len(graph.cities) > 1 {
				graph.changePrice()
				graph.lock.Unlock()
				time.Sleep(300 * time.Millisecond)
			} else {
				graph.lock.Unlock()
			}
		} else {
			graph.lock.Unlock()
			break
		}
	}
}

func (graph *Airport) changePrice() {

	A := rand.Intn(len(graph.cities))
	cityA := graph.cities[A]
	sizeOfFlights := len(graph.flights[cityA])

	if sizeOfFlights > 0 {
		fmt.Println("Change price:")
		B := rand.Intn(sizeOfFlights)
		cityB := graph.flights[cityA][B].direction
		var newPrice int
		for {
			newPrice = (rand.Intn(20) + 1) * 100
			if newPrice != graph.flights[cityA][B].price {
				graph.flights[cityA][B].price = newPrice
				break
			}
		}

		for i, flight := range graph.flights[cityB] {
			if flight.direction == cityA {
				graph.flights[cityB][i].price = newPrice
				break
			}
		}
		graph.display()
	}
}

//////////////////////////////////////////////////////////////////////////////

//потік, що видаляє і додає рейси між містами;

func AddDeleteFlights(graph *Airport) {
	defer graph.wg.Done()
	for {
		graph.lock.Lock()
		if len(queueOfCitiesToAdd) > 0 {
			if len(graph.cities) > 1 {
				random := rand.Intn(3)
				if random >= 0 && random < 2 {
					graph.AddFlight()
				} else {
					graph.DeleteFlight()
				}
			}
			graph.lock.Unlock()
			time.Sleep(100 * time.Millisecond)
		} else {
			graph.lock.Unlock()
			break
		}
	}
}

func getB(A int, graph *Airport) int {
	var B int
	for {
		B = rand.Intn(len(graph.cities))
		if B != A {
			return B
		}
	}
}

func (graph *Airport) AddFlight() {
	A := rand.Intn(len(graph.cities))
	cityA := graph.cities[A]
	var B int
	var cityB *City
	B = getB(A, graph)
	cityB = graph.cities[B]
	if !cityA.containsFlightTo(cityB, graph) {
		fmt.Println("Add flight")
		price := (rand.Intn(20) + 1) * 100
		flight1 := Flight{price, cityA}
		flight2 := Flight{price, cityB}
		graph.flights[cityA] = append(graph.flights[cityA], &flight2)
		graph.flights[cityB] = append(graph.flights[cityB], &flight1)
		graph.display()
	}
}

func (cityA *City) containsFlightTo(cityB *City, graph *Airport) bool {
	for _, dir := range graph.flights[cityA] {
		if dir.direction == cityB {
			return true
		}
	}
	return false
}

func (graph *Airport) DeleteFlight() {
	A := rand.Intn(len(graph.cities))
	cityA := graph.cities[A]
	sizeOfDirections := len(graph.flights[cityA])
	if sizeOfDirections > 0 {
		fmt.Println("Delete Flight:")
		B := rand.Intn(sizeOfDirections)
		cityB := graph.flights[cityA][B].direction
		graph.deleteDirection(cityA, cityB)
		graph.deleteDirection(cityB, cityA)
		graph.display()
	}
}

func (graph *Airport) deleteDirection(cityA *City, cityB *City) {
	flightsFromA := graph.flights[cityA]
	for i, flight := range flightsFromA {
		if flight.direction == cityB {
			flightsFromA = append(flightsFromA[:i], flightsFromA[i+1:]...)
			graph.flights[cityA] = flightsFromA
			break
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////

//потік, що видаляє старі міста і додає нові;

func AddDeleteCities(graph *Airport) {
	defer graph.wg.Done()
	for {
		graph.lock.Lock()
		if len(queueOfCitiesToAdd) > 0 {
			random := rand.Intn(3)
			if random >= 0 && random < 2 {
				cityIndex := rand.Intn(len(queueOfCitiesToAdd))
				cityToAdd := queueOfCitiesToAdd[cityIndex]
				queueOfCitiesToAdd = append(queueOfCitiesToAdd[:cityIndex], queueOfCitiesToAdd[cityIndex+1:]...)
				graph.AddCity(cityToAdd)
			} else {
				graph.DeleteCity()
			}
		} else {
			graph.lock.Unlock()
			break
		}
		graph.lock.Unlock()
		time.Sleep(300 * time.Millisecond)
	}
}

func (graph *Airport) AddCity(city *City) {
	fmt.Println("Add city:")
	graph.flights[city] = nil
	graph.cities = append(graph.cities, city)
	graph.display()
}

func (graph *Airport) DeleteCity() {
	deletedCity := graph.deleteNode()
	if deletedCity == nil {
		return
	} else {
		fmt.Println("Delete city")
	}
	graph.deleteFlightsToDeletedCity(deletedCity)
	graph.display()
}

func (graph *Airport) deleteNode() *City {
	amountOfCities := len(graph.cities)
	if amountOfCities == 0 {
		return nil
	}
	toDeleteIndex := rand.Intn(amountOfCities)
	toDeleteCity := graph.cities[toDeleteIndex]
	graph.cities = append(graph.cities[:toDeleteIndex], graph.cities[(toDeleteIndex+1):]...)
	return toDeleteCity
}

func (graph *Airport) deleteFlightsToDeletedCity(deletedCity *City) {
	flightsFromDeletedCity := graph.flights[deletedCity]
	delete(graph.flights, deletedCity)
	for _, flightFromDeletedCity := range flightsFromDeletedCity {
		cities := graph.flights[flightFromDeletedCity.direction]
		for i, city := range cities {
			if city.direction == deletedCity {
				cities = append(cities[:i], cities[(i+1):]...)
				graph.flights[flightFromDeletedCity.direction] = cities
				break
			}
		}
	}
}

////////////////////////////////////////////////////////////////////////////////

//потоки, що визначають чи є шлях від довільного міста А до довільного міста Б,
//і яка ціна такої поїздки (якщо прямого шляху немає, то знайти будь-який шлях з існуючих)

func SearchRouteFromAtoB(graph *Airport) {
	defer graph.wg.Done()
	for {
		graph.lock.RLock()
		if len(queueOfCitiesToAdd) > 0 {
			if len(graph.cities) > 1 {
				i := 0
				head := graph.cities[rand.Intn(len(graph.cities))]
				var destination *City
				for {
					destination = graph.cities[rand.Intn(len(graph.cities))]
					if destination != head {
						break
					}
				}
				price := 0
				found := false
				fmt.Println("Search route from city " + head.name + " to " + destination.name + ":")
				graph.searchRoute(&i, head, nil, head, destination, &price, &found)
				graph.makeCitiesNonVisited()
				if found {
					fmt.Printf("Price of such route costs %v\n\n", price)
				} else {
					fmt.Println("Such route doesn't exist")
					fmt.Println()
				}
				graph.lock.RUnlock()
				time.Sleep(300 * time.Millisecond)
			} else {
				graph.lock.RUnlock()
				time.Sleep(100 * time.Millisecond)
			}
		} else {
			graph.lock.RUnlock()
			break
		}
	}
}

func (graph *Airport) searchRoute(i *int, head *City, prev *City, current *City, destination *City, price *int, found *bool) {
	if *i > 0 && current == head || current.visited {
		return
	}
	temp := *price
	for j, flight := range graph.flights[current] {
		if !*found {
			if j == len(graph.flights[current])-1 {
				current.visited = true
			}
			*price = temp
			if flight.direction == destination {
				*price += flight.price
				*found = true
				return
			} else if flight.direction == prev {
				continue
			} else {
				*i++
				*price += flight.price
				graph.searchRoute(i, head, current, flight.direction, destination, price, found)
			}
		} else {
			break
		}
	}
}

func (graph *Airport) makeCitiesNonVisited() {
	for _, city := range graph.cities {
		city.visited = false
	}
}

/////////////////////////////////////////////////////////////////////////////////////
