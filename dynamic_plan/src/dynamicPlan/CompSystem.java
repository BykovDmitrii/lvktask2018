package dynamicPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Comparator;
import java.util.Vector;

public class CompSystem {
    private int time;//текущее время в системе
    private int curTask;//порядковый номер(индекс в векторе + 1) прошлой программы или 0, если система простаивала
    private int runtime;//максимальное время работы системы
    private Vector<Task> tasks;//вектор всех программ
    //конструтор, иницализирующий систему
    public CompSystem(int n){
        tasks = new Vector<Task>();
        runtime = n;
        time = 0;
        curTask = 0;
    }
//добавляем программу в наш вектор
    public void addTask(Task t){
        tasks.addElement(t);
    }

    public boolean isFinished(){
        return !(runtime>time);
    }
//сортируем наши программы по приоритетам
    public void sortTask() {
        tasks.sort(new Comparator<Task>(){
            public int compare(Task a,Task b){
                return a.priority - b.priority;
            }
        });
    }

    public int getRuntime() {
        return runtime;
    }

    private Element runTask(Task task, int curNum, Element root, Document document) throws ParserConfigurationException {
        //если текущий элемент равен выводимому в прошлый момент времени, то выполнимся и вернемся
        if(curNum+1 == curTask){
            task.runTime();//выполнемся(запускаем функцияю которая изменяет счетчики ответсвенные за выполнение и за период)
            return (Element) root;
        }
        curTask=curNum+1;
//создаем новую строчку, добавляем ее  и выполнемся
        Element elem;
        if(!task.wasUsed()){//
            elem = document.createElement("start");
        }
        else{
            elem = document.createElement("continue");
        }
        elem.setAttribute("name",task.getName());
        elem.setAttribute("time",Integer.toString(time));
        root.appendChild(elem);
        task.runTime();
        return root;//возвращаем обновленный вывод
    }

    private int min(int a,int b){
        if(a>b)
            return b;
        return a;
    }

    public Element nextTime(Element root,Document document) throws ParserConfigurationException {
        int isRun = 1;//была ли запущена задача
        int maxTimeUnchanged = runtime;// время, за которое в системе не произошли изменения
        for(int i = 0; i < tasks.size(); i++){
            if((isRun == 1)&&(!(tasks.elementAt(i).isFinished()))) {
                isRun = 0;
                root = this.runTask(tasks.elementAt(i), i,root,document);
                maxTimeUnchanged = min(tasks.elementAt(i).restDurarion(), maxTimeUnchanged);
            }
            else {
                tasks.elementAt(i).waitTime();
                maxTimeUnchanged = min(tasks.elementAt(i).restPeriod(),maxTimeUnchanged);
            }
        }
        if(isRun == 1)
            curTask = 0;
        time++;
//пропустим промежуток, где ничего не поменяется
        if (maxTimeUnchanged > 1){
            for (int i = 0; i < tasks.size(); i++)
                if (curTask == i + 1)
                    tasks.elementAt(i).runTime(maxTimeUnchanged - 1);
                else
                    tasks.elementAt(i).waitTime(maxTimeUnchanged - 1);
            time += maxTimeUnchanged - 1;
        }
        return root;
    }
};
