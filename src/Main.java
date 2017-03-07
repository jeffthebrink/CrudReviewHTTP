import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> userHashMap = new HashMap<>();
    static ArrayList<Person> personArrayList = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("Starting Person List App...");

        Spark.init();

        Spark.get("/", (request, response) -> {
                    Session session = request.session();
                    String loginName = session.attribute("loginName");
                    User user = userHashMap.get(loginName);

                    HashMap m = new HashMap();
                    if (user == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        m.put("name", user.name);
                        m.put("personArrayList", personArrayList);
                        return new ModelAndView(m, "home.html");
                    }
                },
                new MustacheTemplateEngine()
        );

        Spark.post("/login", (request, response) -> {
            String loginName = request.queryParams("loginName");
            if (!loginName.isEmpty()) {
                User user = new User(loginName);
                userHashMap.put(loginName, user);
            }
            Session session = request.session();
            session.attribute("loginName", loginName);
            response.redirect("/");
            return "";
        });

        Spark.post("/logout", (request, response) -> {
            Session session = request.session();
            session.invalidate();
            response.redirect("/");
            return "";
        });

        Spark.post("/createEntry", (request, response) -> {
            String name = request.queryParams("name");
            int age = Integer.parseInt(request.queryParams("age"));
            String city = request.queryParams("city");
            String country = request.queryParams("country");
            int id = personArrayList.size();
            if (!name.isEmpty()) {
                Person person = new Person(name, age, city, country, id);
                personArrayList.add(person);
            }
            response.redirect("/");
            return "";
        });

        Spark.get("/edit", (request, response) -> {
                    HashMap m = new HashMap();
                    int id = Integer.parseInt(request.queryParams("id"));
                    for (Person p : personArrayList) {
                        if (id == p.id) {
                            m.put("person", p);
                        }
                    }
                    return new ModelAndView(m, "edit_entry.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post("/editEntry", (request, response) -> {
            String name = request.queryParams("name");
            int age = Integer.parseInt(request.queryParams("age"));
            String city = request.queryParams("city");
            String country = request.queryParams("country");
            int id = Integer.parseInt(request.queryParams("id"));
            if (!name.isEmpty()) {
                Person person = new Person(name, age, city, country, id);
                personArrayList.set((id), person);
            }
            response.redirect("/");
            return "";
        });

        Spark.get("/delete", (request, response) -> {
                    int id = Integer.parseInt(request.queryParams("id"));
                    HashMap m = new HashMap();
                    for (Person p : personArrayList) {
                        if (id == p.id) {
                            m.put("person", p);
                        }
                    }
                    return new ModelAndView(m, "delete_entry.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post("/deleteEntry", (request, response) -> {
            int id = Integer.parseInt(request.queryParams("id"));
            personArrayList.remove((id));
            response.redirect("/");
            return "";
        });
    }
}
