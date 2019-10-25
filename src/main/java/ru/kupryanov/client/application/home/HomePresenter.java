package ru.kupryanov.client.application.home;

import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import ru.kupryanov.client.application.ApplicationPresenter;
import ru.kupryanov.client.dispatch.AsyncCallbackImpl;
import ru.kupryanov.client.place.NameTokens;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import ru.kupryanov.shared.dispatch.*;
import ru.kupryanov.shared.dto.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy> implements HomeUiHandlers {
    interface MyView extends View, HasUiHandlers<HomeUiHandlers> {
        void addTaskInTable(Task task);
        void updateTable(ArrayList<Task> tasks);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.HOME)
    interface MyProxy extends ProxyPlace<HomePresenter> {
    }
    private final DispatchAsync dispatcher;

    @Inject
    HomePresenter(EventBus eventBus, MyView view,MyProxy proxy,DispatchAsync dispatcher) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_APPLICATION);
        this.dispatcher = dispatcher;
        getView().setUiHandlers(this);
        updateTable();
    }
    @Override
    public void addTaskInTable(long id){
        dispatcher.execute(new GetTaskAction(id), new AsyncCallbackImpl<GetTaskResult>() {
            @Override
            public void onSuccess(GetTaskResult getTaskResult) {
                getView().addTaskInTable(getTaskResult.getTasks());
            }
        });
    }
    @Override
    public void updateTable() {
        dispatcher.execute(new GetTasksAction(), new AsyncCallbackImpl<GetTasksResult>() {
            @Override
            public void onSuccess(GetTasksResult result) {
                ArrayList<Task> tasks = result.getTasks();
                tasks.sort(new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        return o1.getDue().compareTo(o2.getDue());
                    }
                });
                getView().updateTable(tasks);
            }
        });
    }
    @Override
    public void saveTask(String taskString, Date due) {
        Task task = new Task(taskString, due);
        dispatcher.execute(new AddTaskAction(task), new AsyncCallbackImpl<AddTaskResult>() {
            @Override
            public void onSuccess(AddTaskResult addTaskResult) {
            }
        });
    }
    @Override
    protected void onReveal() {
        super.onReveal();
    }

}
