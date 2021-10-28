package ex0.algo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;


import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

public class MyAlgo  implements ElevatorAlgo{
    private Building building;
    private DataOfElevator[] data;
    private Elevator[] elevators;
    private ArrayList<Integer> express;

    public MyAlgo(Building building){
        this.building = building;
        elevators = new Elevator[building.numberOfElevetors()];
        data = new DataOfElevator[building.numberOfElevetors()];
        express = new ArrayList<>();
        
        PriorityQueue<Integer> speed = new PriorityQueue<Integer>( new Comparator<Integer>() {
            @Override
            public int compare(Integer speed1, Integer speed2) {
                return (int) (elevators[speed1].getSpeed() - elevators[speed2].getSpeed());
            }
        });
        for (int i = 0; i < elevators.length; i++) {
            elevators[i] = building.getElevetor(i);
            speed.add(i);
            if(i % 2 == 0){
                data[i] = new DataOfElevator(-1);
                data[i].add(building.minFloor());
            }
            else {
                data[i] = new DataOfElevator(1);
                data[i].add(building.maxFloor());  
            } 
        }
        int count = building.numberOfElevetors() / 4;
        while (!(speed.isEmpty()) && count-- > 0) {
            express.add(speed.peek());
            data[speed.poll()].isExprees = true;
        }
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public String algoName() {
        return "TheBestAlgoritemEver!";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        int elev = findElevator(c);
        if (data[elev].isEmpty()) {
            data[elev].dest = c.getSrc();
            data[elev].chageDirection(c.getType());
        }
        data[elev].add(c.getSrc());
        data[elev].add(c.getDest());
        if (elevators[elev].getState() == 0) {
            elevators[elev].goTo(data[elev].dest);
        }
        else{
            elevators[elev].stop(data[elev].dest);
        }
        return elev;
    }
    private int findElevator(CallForElevator c) {
        ArrayList<ArrayList> values =  new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(new ArrayList<>());
        }
        for (int i = 0; i < elevators.length; i++) {
            int value = valuePerCall(i, c);
            if (value != -1) {
                values.get(value).add(i);
            }
        }
        for (int i = values.size() - 1; i >= 0; i--) {
            if (!values.get(i).isEmpty()) {
                return closeElevator(values.get(i), c);
            }
        }
        return 0;
    }
    private int valuePerCall(int elev, CallForElevator c){
        if(!data[elev].isExprees && data[elev].isEmpty()){
            return 7;
        }
        if (data[elev].state == -c.getType()) {
            return 0;
        }
        if((c.getType() == 1 && elevators[elev].getPos() >= c.getSrc()) || (c.getType() == -1 && elevators[elev].getPos() <= c.getSrc())){
            return 0;
        }
        int parameter = (building.maxFloor() - building.minFloor()) / 4 + 1;
        if (data[elev].isExprees) {
            if (Math.abs(c.getSrc() - c.getDest()) >= parameter) {
                int value = 4;
                Integer[] floors = data[elev].allFloors();
                for (int i = 0; i < floors.length; i++) {
                    if (i == c.getDest() || i == c.getSrc()) {
                        value++;
                    }
                }    
                return value;
            }
            return 0;
        }          
        int value = 1;
        Integer[] floors = data[elev].allFloors();
        for (int i = 0; i < floors.length; i++) {
            if (i == c.getDest() || i == c.getSrc()) {
                value++;
            }
        }     
        return value;
    }
    private int smallFloors(ArrayList<Integer> list, CallForElevator c) {
        int ans = list.get(0);
        int minSize = data[ans].size();
        for (int i = 1; i < list.size(); i++) {
            if (data[list.get(i)].size() < minSize) {
                    minSize = data[list.get(i)].size();
                    ans = list.get(i);
            }
        }
        return ans;
    }
    private int closeElevator(ArrayList<Integer> list, CallForElevator c) {
        int range = Math.abs(elevators[list.get(0)].getPos() - c.getDest());
        for (int i = 1; i < list.size(); i++) {
            int newRange = Math.abs(elevators[list.get(i)].getPos() - c.getDest());
            if (newRange < range) {
                range = newRange;
            } 
        }
        ArrayList<Integer> closes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int newRange = Math.abs(elevators[list.get(i)].getPos() - c.getDest());
            if (newRange == range) {
                closes.add(list.get(i));
            } 
        }
        return smallFloors(closes, c);
    }

    @Override
    public void cmdElevator(int elev) {
        if (elevators[elev].getState() != 0 || data[elev].isEmpty() || data[elev].dest != elevators[elev].getPos()) {
            return;
        }
        data[elev].poll();
        
        if(data[elev].isEmpty()) {
            data[elev].state = 0;
        }
        elevators[elev].goTo(data[elev].dest);
    }

    private int numberOfAllCall(){
        int num = 0;
        for (int i = 0; i < data.length; i++) {
            num += data[i].size();
        }
        return num;
    }
}
class DataOfElevator {
    private PriorityQueue<Integer> elevDest;
    public int dest;
    public int state; // 1, 0 -1
    public boolean isExprees;
    public DataOfElevator(int state) {
        this.state = state;
        elevDest = (state == 1)? new PriorityQueue<>(): new PriorityQueue<>(Comparator.reverseOrder());
        //chageDirection();
    }
    public void chageDirection(int state) {
        this.state = state;
        if (state == -1) {
            PriorityQueue<Integer> tempQueue = new PriorityQueue<>(Comparator.reverseOrder());
            tempQueue.addAll(elevDest);
            elevDest = tempQueue;
        }
        else if (state == 1) {
            PriorityQueue<Integer> tempQueue = new PriorityQueue<>();
            tempQueue.addAll(elevDest);
            elevDest = tempQueue;
        }
    }

    public void add(int floor) {
        elevDest.remove(floor);
        elevDest.add(floor);
    }
    public int poll() {
        int ans = elevDest.poll();
        dest = (!isEmpty())? elevDest.peek(): dest;
        return ans;
    }
    public int peek() {
        return dest;
    }
    public int size() {
        return elevDest.size();
    } 
    public boolean isEmpty() {
        return elevDest.isEmpty();
    } 
    public Integer[] allFloors() {
        return elevDest.toArray(new Integer[this.size()]);
    } 
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return elevDest.toString() + ", " + state + ", " + dest;
    }
}