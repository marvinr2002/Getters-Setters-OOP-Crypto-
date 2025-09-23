import java.io.Console;
import java.util.*;

public class Main {
    private static final String FILE_NAME = "people.enc";
    private static final Scanner SC = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso:");
            System.out.println("  java Main add           -> agregar personas (cifrado)");
            System.out.println("  java Main list          -> listar personas (descifrado)");
            System.out.println("  java Main delete userN  -> eliminar por label (ej: user2)");
            return;
        }

        String cmd = args[0].toLowerCase(Locale.ROOT);
        try {
            switch (cmd) {
                case "add"    -> cmdAdd();
                case "list"   -> cmdList();
                case "delete" -> {
                    if (args.length < 2) { System.out.println("Falta label. Ej: user2"); return; }
                    cmdDelete(args[1]);
                }
                default -> System.out.println("Comando no reconocido. Usa: add | list | delete userN");
            }
        } catch (javax.crypto.AEADBadTagException e) {
            System.out.println(" Contraseña incorrecta o archivo alterado.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cmdAdd() throws Exception {
        char[] pwd = promptPassword("Contraseña para cifrar/abrir: ");
        PersonRepository repo = new PersonRepository(FILE_NAME);
        List<Person> people = repo.load(pwd);

        int nextIndex = people.size() + 1;
        String cont;
        do {
            Person p = new Person();
            p.setLabel("user" + nextIndex++);

            System.out.print("Nombre: ");
            p.setName(SC.nextLine());

            System.out.print("Edad: ");
            while (!SC.hasNextInt()) { System.out.print("Edad inválida. Intenta de nuevo: "); SC.next(); }
            p.setAge(SC.nextInt()); SC.nextLine();

            System.out.print("Género: ");
            p.setGender(SC.nextLine());

            System.out.print("Email: ");
            p.setEmail(SC.nextLine());

            System.out.print("Teléfono: ");
            p.setPhone(SC.nextLine());

            System.out.print("Dirección: ");
            p.setAddress(SC.nextLine());

            people.add(p);
            System.out.print("¿Agregar otra persona? (s/n): ");
            cont = SC.nextLine();
        } while (cont.equalsIgnoreCase("s"));

        repo.save(people, pwd);
        Arrays.fill(pwd, '\0');
        System.out.println("Guardado en " + FILE_NAME);
    }

    private static void cmdList() throws Exception {
        char[] pwd = promptPassword("Contraseña para abrir: ");
        PersonRepository repo = new PersonRepository(FILE_NAME);
        List<Person> people = repo.load(pwd);
        Arrays.fill(pwd, '\0');

        if (people.isEmpty()) { System.out.println("(No hay registros)"); return; }
        System.out.println("\n--- Personas ---");
        for (Person p : people) System.out.println(p);
    }

    private static void cmdDelete(String label) throws Exception {
        char[] pwd = promptPassword("Contraseña para abrir: ");
        PersonRepository repo = new PersonRepository(FILE_NAME);
        List<Person> people = repo.load(pwd);

        boolean removed = people.removeIf(p -> label.equalsIgnoreCase(p.getLabel()));
        if (removed) {
            repo.save(people, pwd);
            System.out.println("Eliminado: " + label);
        } else {
            System.out.println("No se encontró: " + label);
        }
        Arrays.fill(pwd, '\0');
    }

    private static char[] promptPassword(String prompt) {
        Console console = System.console();
        if (console != null) return console.readPassword(prompt);
        System.out.print(prompt); // fallback (IDE)
        return SC.nextLine().toCharArray();
    }
}
