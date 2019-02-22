import dynamicPlan.CompSystem;
import dynamicPlan.Task;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//главный класс
public class DynamicPlan {
    //функция считывания системы из XML
    private static CompSystem getSystemFromXML() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new File("input_dynamic_pri.xml"));//получаем структуру из которой сможем доствть содержимое
        Element root = document.getDocumentElement();//получаем корневой элемент
        CompSystem sys = new CompSystem(Integer.parseInt(root.getAttribute("runtime")));
        NodeList tasks = root.getChildNodes();
        for (int i = 0; i < tasks.getLength(); i++)
            if(tasks.item(i).getNodeType() == 1){
                Element elem = (Element) tasks.item(i);
                sys.addTask(new Task(elem.getAttribute("name"), Integer.parseInt(elem.getAttribute("period")),
                        Integer.parseInt(elem.getAttribute("priority")), Integer.parseInt(elem.getAttribute("duration"))));
            }

        sys.sortTask();//сортируем по приоритетам(так для алгоритма удобней)
        return sys;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException {
        CompSystem sys = getSystemFromXML();// получаем систему
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();//создаем документ в который будет вывод
        Element systemOut = document.createElement("trace");
        document.appendChild(systemOut);
        systemOut.setAttribute("runtime", Integer.toString(sys.getRuntime()));
        while (!sys.isFinished())
            systemOut = sys.nextTime(systemOut, document);
        loadSystemToXML(document);
    }
//функция вывода в XML
    private static void loadSystemToXML(Document document) throws FileNotFoundException, TransformerException {
        Transformer tr= TransformerFactory.newInstance().newTransformer();
        DOMSource source=new DOMSource(document);
        FileOutputStream fos=new FileOutputStream("output.xml");
        StreamResult result = new StreamResult(fos);
        tr.setOutputProperty(OutputKeys.INDENT,"yes");
        tr.transform(source,result);
    }
}
