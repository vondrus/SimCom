package SimCom;

import java.io.*;

@SuppressWarnings("Duplicates")
class GraphCatalogPersistence {

    static GraphCatalog readFromFile(File file) {
        GraphCatalog graphCatalog = new GraphCatalog();
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);

            while (true)
                try {
                    CustomGraph graph = (CustomGraph) objectInputStream.readObject();
                    graphCatalog.add(new GraphCatalogItem(graph, graph.getImage()));
                } catch (EOFException e) {
                    break;
                }
            return graphCatalog;

        } catch (IOException | ClassNotFoundException e) {
            Dialogs.exceptionDialog(e);
        }
        finally {
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
                if (fileInputStream != null)
                    fileInputStream.close();
            }
            catch (IOException e) {
                Dialogs.exceptionDialog(e);
            }
        }
        return null;
    }

    static void writeToFile(File file, GraphCatalog catalog) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);

            for (GraphCatalogItem item : catalog.getItems())
                objectOutputStream.writeObject(item.getGraph());

        } catch (IOException e) {
            Dialogs.exceptionDialog(e);
        }
        finally {
            try {
                if (objectOutputStream != null)
                    objectOutputStream.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                Dialogs.exceptionDialog(e);
            }
        }
    }

}
