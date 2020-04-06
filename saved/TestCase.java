package Shared;
import Shared.*;
import java.util.ArrayList;

public class TestCase {
    public static ArrayList<User> Users () {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Integer> perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,1);
        perms.add(3,1);
        users.add(new User("admin", "admin",perms));
        perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user1", "user1pass",perms));
        perms = new ArrayList<>();
        perms.add(0,0);
        perms.add(1,0);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user2", "pass",perms));
        return users;
    }
    public static ArrayList<Billboard> Billboards () {
        ArrayList<Billboard> billboards = new ArrayList<>();
        billboards.add(new Billboard(
                "Billboard 1",
                "",
                "",
                "Basic message-only billboard",
                "",
                "",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 2",
                "",
                "",
                "",
                "",
                "",
                "Billboard with an information tag and nothing else. Note that the text is word-wrapped. The quick brown fox jumped over the lazy dogs.",
                ""));
        billboards.add(new Billboard(
                "Billboard 3",
                "https://cloudstor.aarnet.edu.au/plus/s/62e0uExNviPanZE/download",
                "",
                "",
                "",
                "",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 4",
                "",
                "iVBORw0KGgoAAAANSUhEUgAAACAAAAAQCAIAAAD4YuoOAAAAKXRFWHRDcmVhdGlvbiBUaW1lAJCFIDI1IDMgMjAyMCAwOTowMjoxNyArMDkwMHlQ1XMAAAAHdElNRQfkAxkAAyQ8nibjAAAACXBIWXMAAAsSAAALEgHS3X78AAAABGdBTUEAALGPC/xhBQAAAS5JREFUeNq1kb9KxEAQxmcgcGhhJ4cnFwP6CIIiPoZwD+ALXGFxj6BgYeU7BO4tToSDFHYWZxFipeksbMf5s26WnAkJki2+/c03OzPZDRJNYcgVwfsU42cmKi5YjS1s4p4DCrkBPc0wTlkdX6bsG4hZQOj3HRDLHqh08U4Adb/zgEMtq5RuH3Axd45PbftdB2wO5OsWc7pOYaOeOk63wYfdFtL5qldB34W094ZfJ+4RlFldTrmW/ZNbn2g0of1vLHdZq77qSDCaSAsLf9kXh9w44PNoR/YSPHycEmbIOs5QzBJsmDHrWLPeF24ZkCe6ZxDCOqHcmxmsr+hsicahss+n8vYb8NHZPTJxi/RGC5IqbRwqH6uxVTX+5LvHtvT/V/R6PGh/iF4GHoBAwz7RD26spwq6Amh/AAAAAElFTkSuQmCC",
                "Billboard with message and picture with data attribute",
                "",
                "",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 5",
                "https://cloudstor.aarnet.edu.au/plus/s/vYipYcT3VHa1uNt/download",
                "",
                "",
                "",
                "",
                "Billboard with picture (with URL attribute) and information text only. The picture is now centred within the top 2/3 of the image and the information text is centred in the remaining space below the image.",
                ""));
        billboards.add(new Billboard(
                "Billboard 6",
                "https://cloudstor.aarnet.edu.au/plus/s/A26R8MYAplgjUhL/download",
                "",
                "Billboard with message, GIF and information",
                "",
                "",
                "This billboard has a message tag, a picture tag (linking to a URL with a GIF image) and an information tag. The picture is drawn in the centre and the message and information text are centred in the space between the top of the image and the top of the page, and the space between the bottom of the image and the bottom of the page, respectively.",
                ""));
        billboards.add(new Billboard(
                "Billboard 7",
                "",
                "",
                "Billboard with message and info",
                "",
                "",
                "Billboard with a message tag, an information tag, but no picture tag. The message is centred within the top half of the screen while the information is centred within the bottom half.",
                ""));
        billboards.add(new Billboard(
                "Billboard 8",
                "",
                "",
                "Billboard with default background and custom-coloured message",
                "#FFC457",
                "",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 9",
                "",
                "",
                "Billboard with custom background and default-coloured message",
                "",
                "#7F3FBF",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 10",
                "https://cloudstor.aarnet.edu.au/plus/s/X79GyWIbLEWG4Us/download",
                "",
                "Default-coloured message",
                "",
                "",
                "Custom-coloured information text",
                "#60B9FF"));
        billboards.add(new Billboard(
                "Billboard 11",
                "",
                "",
                "All custom colours",
                "#FF9E3F",
                "#6800C0",
                "All custom colours",
                "#3FFFC7"));
        billboards.add(new Billboard(
                "Billboard 12",
                "",
                "",
                "The information text is always smaller than the message text",
                "#FFFFFF",
                "#555555",
                "The information text is always smaller than the message text",
                ""));
        billboards.add(new Billboard(
                "Billboard 13",
                "https://cloudstor.aarnet.edu.au/plus/s/EvYVdlUNx72ioaI/download",
                "",
                "",
                "",
                "#FFC457",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 14",
                "https://cloudstor.aarnet.edu.au/plus/s/a2IioOedKQgQwvQ/download",
                "",
                "",
                "",
                "#FF38C3",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 15",
                "https://cloudstor.aarnet.edu.au/plus/s/5fhToroJL0nMKvB/download",
                "",
                "",
                "",
                "#8996FF",
                "",
                ""));
        billboards.add(new Billboard(
                "Billboard 16",
                "",
                "Big Data",
                "",
                "",
                "",
                "",
                ""));
        return billboards;
    }
}
