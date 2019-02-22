package dynamicPlan;

public class Task {
    public String getName() {
        return name;
    }

    private String name;
    protected int priority;
    private int duration;//время выполнения для данной задачи
    private int period;//период для данной задачи
    private int curDuration;//оставшееся время выполнения
    private int curPeriod;//оставшийся период
    private boolean used;//была ли уже использована эта задача в этом периоде

    public boolean wasUsed() {
        return used;
    }
//время оставшееся до конца периода
    int restPeriod(){
        if(curPeriod==period)
            return 0;
        else
            return curPeriod;
    }
    //время, необходимое для завершения текущей задачи
    int restDurarion(){
        return curDuration;
    }
//иницализируем программу(ну, в плане Task)
    public Task(String name, int per, int pri, int dur){
        this.name = name;
        period = curPeriod = per;
        priority = pri;
        duration = curDuration = dur;
        used = false;
    }
//сбрасываем флаги и счетчики
    private void restart(){
        used = false;
        curDuration = duration;
        curPeriod = period;
    }

    public void waitTime(){
        curPeriod--;
        if(curPeriod == 0)
            this.restart();
    }
//пропускаем время не выполняя текушую задачу
    public void waitTime(int n){
        curPeriod-=n;
    }

    public void runTime(){
        curPeriod--;
        curDuration--;
        used = true;
    }
//пропускаем время выполняя текущую задачу
    public void runTime(int n){
        curPeriod-=n;
        curDuration-=n;
    }


    public boolean isFinished(){
        return curDuration==0;
    }
};
