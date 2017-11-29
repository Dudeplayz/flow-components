/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui.iron.list.it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.function.ValueProvider;
import com.vaadin.router.Route;
import com.vaadin.ui.html.Div;
import com.vaadin.ui.html.Label;
import com.vaadin.ui.html.NativeButton;
import com.vaadin.ui.iron.list.IronList;
import com.vaadin.ui.renderers.TemplateRenderer;

/**
 * Test view for {@link IronList}
 * 
 * @author Vaadin Ltd.
 */
@Route("iron-list-test")
public class IronListTestPage extends Div {

    private static final List<String> ITEMS;
    static {
        ITEMS = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            ITEMS.add("Item " + (i + 1));
        }
    }

    private static class Person {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    /**
     * Creates all the components needed for the tests.
     */
    public IronListTestPage() {
        createListWithStrings();
        createDataProviderWithStrings();
        createTemplateFromValueProviderWithPeople();
        createTemplateFromRendererWithPeople();
        createLazyLoadingDataProvider();
        createTemplateWithEventHandlers();
    }

    private void createListWithStrings() {
        IronList<String> list = new IronList<>();
        list.setHeight("100px");

        list.setItems("Item 1", "Item 2", "Item 3");

        NativeButton setListWith3Items = new NativeButton("Change list 1",
                evt -> list.setItems("Item 1", "Item 2", "Item 3"));
        NativeButton setListWith2Items = new NativeButton("Change list 2",
                evt -> list.setItems("Another item 1", "Another item 2"));
        NativeButton setEmptyList = new NativeButton("Change list 3",
                evt -> list.setItems());

        list.setId("list-with-strings");
        setListWith3Items.setId("list-with-strings-3-items");
        setListWith2Items.setId("list-with-strings-2-items");
        setEmptyList.setId("list-with-strings-0-items");

        add(list, setListWith3Items, setListWith2Items, setEmptyList);
    }

    private void createDataProviderWithStrings() {
        IronList<String> list = new IronList<>();
        list.setHeight("100px");

        DataProvider<String, ?> dataProvider1 = DataProvider.ofItems("Item 1",
                "Item 2", "Item 3");
        DataProvider<String, ?> dataProvider2 = DataProvider
                .ofItems("Another item 1", "Another item 2");
        DataProvider<String, ?> dataProvider3 = DataProvider.ofItems();

        list.setDataProvider(dataProvider1);

        NativeButton setProviderWith3Items = new NativeButton(
                "Change dataprovider 1",
                evt -> list.setDataProvider(dataProvider1));
        NativeButton setProviderWith2Items = new NativeButton(
                "Change dataprovider 2",
                evt -> list.setDataProvider(dataProvider2));
        NativeButton setEmptyProvider = new NativeButton(
                "Change dataprovider 3",
                evt -> list.setDataProvider(dataProvider3));

        list.setId("dataprovider-with-strings");
        setProviderWith3Items.setId("dataprovider-with-strings-3-items");
        setProviderWith2Items.setId("dataprovider-with-strings-2-items");
        setEmptyProvider.setId("dataprovider-with-strings-0-items");

        add(list, setProviderWith3Items, setProviderWith2Items,
                setEmptyProvider);
    }

    private void createTemplateFromValueProviderWithPeople() {
        IronList<Person> list = new IronList<>();
        list.setHeight("100px");

        DataProvider<Person, ?> dataProvider1 = DataProvider
                .ofCollection(createPeople(3));
        DataProvider<Person, ?> dataProvider2 = DataProvider
                .ofCollection(createPeople(2));
        DataProvider<Person, ?> dataProvider3 = DataProvider
                .ofCollection(createPeople(0));

        list.setDataProvider(dataProvider1);
        list.setRenderer(Person::getName);

        NativeButton setProviderWith3Items = new NativeButton(
                "Change dataprovider 1", evt -> {
                    list.setRenderer(Person::getName);
                    list.setDataProvider(dataProvider1);
                });
        NativeButton setProviderWith2Items = new NativeButton(
                "Change dataprovider 2", evt -> {
                    list.setDataProvider(dataProvider2);
                    list.setRenderer(person -> String.valueOf(person.getAge()));
                });
        NativeButton setEmptyProvider = new NativeButton(
                "Change dataprovider 3",
                evt -> list.setDataProvider(dataProvider3));

        list.setId("dataprovider-with-people");
        setProviderWith3Items.setId("dataprovider-with-people-3-items");
        setProviderWith2Items.setId("dataprovider-with-people-2-items");
        setEmptyProvider.setId("dataprovider-with-people-0-items");

        add(list, setProviderWith3Items, setProviderWith2Items,
                setEmptyProvider);
    }

    private void createTemplateFromRendererWithPeople() {
        IronList<Person> list = new IronList<>();
        list.setHeight("100px");

        List<Person> people = createPeople(3);
        DataProvider<Person, ?> dataProvider = DataProvider
                .ofCollection(people);

        list.setDataProvider(dataProvider);
        list.setRenderer(TemplateRenderer
                .<Person> of("[[item.name]] - [[item.age]] - [[item.user]]")
                .withProperty("name", Person::getName)
                .withProperty("age", Person::getAge)
                .withProperty("user", person -> person.getName().toLowerCase()
                        .replace(" ", "_")));

        NativeButton update = new NativeButton("Update item 1", evt -> {
            Person item = people.get(0);
            item.setName(item.getName() + " Updated");
            list.getDataProvider().refreshItem(item);
        });

        list.setId("template-renderer-with-people");
        update.setId("template-renderer-with-people-update-item");

        add(list, update);
    }

    private void createLazyLoadingDataProvider() {
        IronList<String> list = new IronList<>();
        list.setHeight("100px");

        Label message = new Label();

        DataProvider<String, ?> dataProvider = DataProvider
                .fromCallbacks(query -> {
                    List<String> result = queryStrings(query);
                    message.setText("Sent " + result.size() + " items");
                    return result.stream();
                }, this::countStrings);

        list.setDataProvider(dataProvider);

        list.setId("lazy-loaded");
        message.setId("lazy-loaded-message");

        add(list, message);
    }

    private void createTemplateWithEventHandlers() {
        IronList<String> list = new IronList<>();
        list.setHeight("100px");

        Label message = new Label();

        List<String> items = new ArrayList<>(Arrays.asList("Clickable item 1",
                "Clickable item 2", "Clickable item 3"));

        list.setRenderer(TemplateRenderer.<String> of(
                "<div on-click='remove' id='template-events-item-[[index]]'>[[item.label]]</div>")
                .withProperty("label", ValueProvider.identity())
                .withEventHandler("remove", item -> {
                    items.remove(item);
                    list.getDataCommunicator().reset();
                    message.setText(item + " removed");
                }));

        list.setItems(items);
        list.setId("template-events");
        message.setId("template-events-message");

        add(list, message);
    }

    private List<Person> createPeople(int amount) {
        List<Person> people = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            Person person = new Person();
            person.setName("Person " + (i + 1));
            person.setAge(i + 1);
            people.add(person);
        }
        return people;
    }

    private List<String> queryStrings(Query<String, Void> query) {
        return ITEMS.subList(Math.min(query.getOffset(), ITEMS.size() - 1),
                Math.min(query.getOffset() + query.getLimit(), ITEMS.size()));
    }

    private int countStrings(Query<String, Void> query) {
        return ITEMS.size();
    }

}
