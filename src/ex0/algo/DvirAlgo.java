package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.*;
import java.util.logging.Level;

class node implements Comparable<node> {
    int index;
    double speed;

    node(int index, double speed) {
        this.index = index;
        this.speed = speed;
    }

    @Override
    public int compareTo(node b) {
        if (this.speed > b.speed) return 1;
        if (this.speed < b.speed) return -1;
        return 0;
    }
}

public class DvirAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1;
    private int _direction;
    private Building _building;
    final int parameter;
    ArrayList<ArrayList<Integer>> Up;
    ArrayList<ArrayList<Integer>> Down;
    boolean first = true;
    private int[] Typo;  ///0 - UpLook , 1 - DownLook , 2 - UpEx , 3 - DownEx
    boolean fl = true;
    int count = 0;
    int[] arr = new int[9];

    public DvirAlgo(Building b) {
        this._building = b;
        parameter = (this._building.maxFloor() - this._building.minFloor()) / 4;
        Typo = new int[_building.numberOfElevetors()];
        TypeOfElev(Typo);
        Up = new ArrayList<>();
        Down = new ArrayList<>();
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            Up.add(new ArrayList<Integer>());
            Down.add(new ArrayList<Integer>());
        }
    }

    private void TypeOfElev(int[] type) {   /// the fastest elevators are the exspress;
        if (_building.numberOfElevetors() < 3) {
            for (int i = 0; i < _building.numberOfElevetors(); i++) {
                Typo[i] = i;
            }
        } else {
            PriorityQueue<node> speed = new PriorityQueue<node>();
            for (int i = 0; i < _building.numberOfElevetors(); i++) {
                node n = new node(i, _Elev(i).getSpeed());
                speed.add(n);
            }
            int count = 0;
            while (count < 3) {
                for (int i = 0; i < _building.numberOfElevetors() / 4; i++) {
                    Typo[speed.poll().index] = count;
                }
                count++;
            }
            while (!(speed.isEmpty())) {
                Typo[speed.poll().index] = 3;
            }
        }
    }


    @Override
    public Building getBuilding() {
        return this._building;
    }

    @Override
    public String algoName() {
        return "MyElevatorAlgo";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
//        System.out.println(c);
        int relevant = 0;
        if (_building.numberOfElevetors() < 3) {
            relevant = smallcase(c);
        } else {
            TypeOfElev(Typo);
            int distance = (c.getSrc() - c.getDest());
            if (distance < 0) {    /// Going up
                if (this._building.numberOfElevetors() >= 3 && Math.abs(distance) >= parameter) {
                    relevant = FindCloseExpress(c.getSrc(), 1);
                } else {
                    relevant = FindCloseLook(c.getSrc(), 1);
                }
                if (Up.get(relevant).isEmpty() && _Elev(relevant).getPos() == Elevator.LEVEL) startOver(relevant, UP);
                Up.get(relevant).add(c.getSrc());
                Up.get(relevant).add(c.getDest());
                Collections.sort(Up.get(relevant));
            } else {
                if (distance > 0) { /// Going down
                    if (this._building.numberOfElevetors() >= 3 && Math.abs(distance) >= parameter) {
                        relevant = FindCloseExpress(c.getSrc(), -1);
                    } else {
                        relevant = FindCloseLook(c.getSrc(), -1);
                    }
                }

                Down.get(relevant).add(c.getSrc());
                Down.get(relevant).add(c.getDest());
                Collections.sort(Down.get(relevant), Collections.reverseOrder());
            }

            if (distance == 0) throw new RuntimeException("you call the elevator to your level");
        }
        return relevant;
    }

    public int smallcase(CallForElevator c) {
        if (_building.numberOfElevetors() == 1) {
            Up.add(new ArrayList<Integer>());
            Down.add(new ArrayList<Integer>());
            {
                int dis = c.getSrc() - c.getDest();
                if (dis < 0)  //Going Up
                {
                    if (_Elev(0).getPos() < c.getSrc()) {
                        Up.get(0).add(c.getSrc());
                        Up.get(0).add(c.getDest());
                        Collections.sort(Up.get(0));
                    } else {
                        Up.get(1).add(c.getSrc());
                        Up.get(1).add(c.getDest());
                        Collections.sort(Up.get(1));
                    }
                    if (Up.get(0).isEmpty()) {
                        for (int i = 0; i < Up.get(1).size(); i++) {
                            Up.get(0).add(Up.get(1).get(i));
                        }
                        Up.get(1).clear();
                        Typo[0] = 1;
                    }
                    return 0;
                } else { // going down
                    if (_Elev(0).getPos() > c.getSrc()) {
                        Down.get(0).add(c.getDest());
                        Down.get(0).add(c.getSrc());
                        Collections.sort(Down.get(0), Collections.reverseOrder());
                    } else {
                        Down.get(1).add(c.getDest());
                        Down.get(1).add(c.getSrc());
                        Collections.sort(Down.get(1), Collections.reverseOrder());
                    }
                    if (Down.get(0).isEmpty()) {
                        Typo[0] = 0;
                        for (int i = 0; i < Down.get(1).size(); i++) {
                            Down.get(0).add(Down.get(1).get(i));
                        }
                        Down.get(1).clear();
                    }
                }
            }
            return 0;
        }
        if(_building.numberOfElevetors() == 2){
            Typo[0] = 0;
            Typo[1] =1;
            int dis = c.getSrc() - c.getDest();
            if (dis < 0)  //Going Up
            {
                if(c.getSrc() > _Elev(0).getPos()){
                    Up.get(0).add(c.getSrc());
                    Up.get(0).add(c.getDest());
                    Collections.sort(Up.get(0));
                }
                if(c.getSrc() < _Elev(0).getPos()) {
                Up.get(1).add(c.getSrc());
                Up.get(1).add(c.getDest());
                Collections.sort(Up.get(1)); }
                if(Up.get(0).isEmpty())
                {
                    _Elev(0).goTo(Up.get(1).get(0));
                    for(int i=0; i<Up.get(1).size();i++)
                    {
                        Up.get(0).add(Up.get(1).get(i));
                    }
                    Up.get(1).clear();
                }
                return 0;
            }
            else{  ///Going Down
                if(_Elev(1).getPos() > c.getSrc())
                {
                    Down.get(1).add(c.getSrc());
                    Down.get(1).add(c.getDest());
                    Collections.sort(Down.get(1), Collections.reverseOrder());
                }
                if(_Elev(1).getPos() < c.getSrc())
                {
                    Down.get(0).add(c.getSrc());
                    Down.get(0).add(c.getDest());
                    Collections.sort(Down.get(1), Collections.reverseOrder());
                }
                if(Down.get(1).isEmpty())
                {
                    _Elev(1).goTo(Down.get(0).get(0));
                    for(int i=0; i<Down.get(0).size();i++)
                    {
                        Down.get(1).add(Down.get(0).get(i));
                    }
                    Down.get(0).clear();
                }
            }
            }
        return 1;

    }

    public void startOver(int i , int pos)
    {
        if(pos==1)
        {
            System.out.println(_Elev(i).getPos());
            _Elev(i).stop(_Elev(i).getPos() - 5);
            System.out.println(_Elev(i).getPos());
        }
        else {
            if (pos == -1)
            {
                System.out.println(_Elev(i).getPos());
                _Elev(i).stop(_Elev(i).getPos() + 5);
                System.out.println(_Elev(i).getPos());
            }
        }
    }

    private int FindCloseLook(int src, int status) {
        boolean f = false;
        int max = Integer.MAX_VALUE;
        int index = 3;
        if (status == -1)  //going down
        {
            for (int i = 0; i < _building.numberOfElevetors(); i = i + 1) {
                if (Typo[i] == 1) {
                    if (Down.get(i).size() < 45) {
                        int dis = Math.abs(_Elev(i).getPos() - src);
                        if (dis < max && dis > 0) {
                            max = dis;
                            index = i;
                        }
                    }
                }
            }
            if (!f) {
              //  NoAction();
                index = findEmptyElevDown(index);
                if(index==3 && fl==true && _building.numberOfElevetors()>=3){fl=false; index = FindCloseExpress(src,status);};
               }
            }
         else {  ///going up
            for (int i = 0; i < _building.numberOfElevetors(); i = i + 1) {
                if (Typo[i] == 0) {
                    if (Up.get(i).size() < 45) {
                        int dis = Math.abs(_Elev(i).getPos() - src);
                        if (dis < max && dis > 0) {
                            max = dis;
                            index = i;
                        }
                    }
                }
            }
            if (!f) {
              //  NoAction();
                index = findEmptyElevUp(index);
                //if(index==3 && fl==true){ this.fl =false ; index = FindCloseExpress(src,status);};
              }
        }
         fl=true;
        return index;
    }
    private int FindCloseExpress(int src, int status) {
        boolean f = false;
        int max = Integer.MAX_VALUE;
        int index = 0;
        if (status == -1) /// going down
        {
            for (int i = 0; i < _building.numberOfElevetors(); i = i + 1) {
                if (Typo[i] == 3) {
                    if (Down.get(i).size() < 40) {
                        f = true;
                        int dis = Math.abs(_Elev(i).getPos() - src);
                        if (dis < max && dis > 0) {
                            max = dis;
                            index = i;
                        }
                    }
                }
            }
            if (!f)
            {
               // NoAction();
                index = findEmptyElevDown(index);
            }

        } else {
            for (int i = 0; i < _building.numberOfElevetors(); i = i + 1) {
                if (Typo[i] == 2) {
                    if (Up.get(i).size() < 45) {
                        int dis = Math.abs(_Elev(i).getPos() - src);
                        if (dis < max && dis > 0) {
                            max = dis;
                            index = i;
                        }
                    }
                }
            }
            if (!f){
            //    NoAction();
                index = findEmptyElevUp(index);
            }
        }
        fl =true;
        return index;
    }

    public int findEmptyElevUp(int index ) {
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (Up.get(i).isEmpty()) {
                return i;
            }
        }

        return index;
    }

    public int findEmptyElevDown(int index) {
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (Typo[i]%2==1) {
                if(Down.get(i).isEmpty()) return i;
            }
        }

        return index;
    }

    public Elevator _Elev(int i) {
        return (this._building.getElevetor(i));
    }

    public int get_direction() {
        return _direction;
    }

    @Override
    public void cmdElevator(int elev) {
        if (this._building.numberOfElevetors() <= 0)
            throw new RuntimeException("there is no elevators in this building");
        else {
            fn(elev);
        }
    }

    public void GoDown(int index){
        if (_Elev(index).getState() == Elevator.LEVEL && !(Down.get(index).isEmpty())) {
            if (_Elev(index).getPos() == Down.get(index).get(0) ) {
                Down.get(index).remove(0);
            }
            if (!(Down.get(index).isEmpty())) {
                _Elev(index).goTo(Down.get(index).get(0));
//                System.out.println(Up.get(index).size() - 1 + " <-Size index ->" + index + "  Type -> " +Typo[index] +  "  The next floor" + Up.get(index).get(0));                _Elev(index).goTo(Down.get(index).get(0));
               _Elev(index).stop(Down.get(index).get(0));
            }
        }
        if(_Elev(index).getState()==Elevator.LEVEL && Down.get(index).isEmpty())
        {
            if(_Elev(index).getPos() < _building.maxFloor() - 4) {
                _Elev(index).goTo(_Elev(index).getPos()+3 );
                System.out.println(" ending it" + _Elev(index).getState());
            }
        }
    }
    public void GoUp(int index)
    {
        if (_Elev(index).getState() == Elevator.LEVEL && !(Up.get(index).isEmpty())) {
            if (_Elev(index).getPos() == Up.get(index).get(0) && _Elev(index).getState() == Elevator.LEVEL) {
                Up.get(index).remove(0);
            }
            if (!(Up.get(index).isEmpty())) {
                System.out.println(Up.get(index).size() - 1 + " <-Size index ->" + index + "  Type -> " +Typo[index] +  "  The next floor" + Up.get(index).get(0));

                _Elev(index).goTo(Up.get(index).get(0));
                _Elev(index).stop(Up.get(index).get(0));
            }
           else {
                if(Up.get(index).isEmpty() && _Elev(index).getState()==Elevator.LEVEL)
                _Elev(index).goTo(_building.minFloor());
            }

        }
        if(_Elev(index).getPos()==Elevator.LEVEL && Up.get(index).isEmpty())
        {
            if(_Elev(index).getPos()> _building.minFloor()+3)
            {
                System.out.println("Doing it " + _Elev(index).getPos());
                _Elev(index).goTo(_Elev(index).getPos()-3);
                System.out.println("Ending it " + _Elev(index).getState());

            }
        }
    }

    public void fn(int index) {
        if (Typo[index] % 2 == 1) { GoDown(index); }

        if (Typo[index] % 2 == 0) { { GoUp(index); }
        }
    }
public void NoAction()
{
    for(int i=0; i<_building.numberOfElevetors();i++)
    {
        if(Up.get(i).isEmpty() &&  Down.get(i).isEmpty())
        {
            changeDir(i);
        }
    }
}

    public void changeDir(int i) {
        if(Typo[i]==1) Typo[i]=0;
        else if(Typo[i]==0) Typo[i]=1;
        else if(Typo[i]==3) Typo[i]=2;
        else  Typo[i]=3;


 /*       if ((this.Typo[i] == 0 || this.Typo[i] == 2) && (Up.get(i).isEmpty() && _Elev(i).getState() == Elevator.LEVEL)) {
            Down.get(i).clear();
            if (this.Typo[i] == 0) {
                this.Typo[i] = 1;
            } else {
                this.Typo[i] = 3;
            }

        } else if ((this.Typo[i] == 1 || this.Typo[i] == 3) && (Down.get(i).isEmpty() && _Elev(i).getState() == Elevator.LEVEL)) {
            Up.get(i).clear();
            if (this.Typo[i] == 1) {
                this.Typo[i] = 0;
            } else {
                this.Typo[i] = 2;
            }
        }*/
    }
}


////public AlotOfReq
// public move Elevetor
//public MaxReq