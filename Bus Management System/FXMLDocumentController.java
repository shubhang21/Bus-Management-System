package databas;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class FXMLDocumentController implements Initializable {    
    
 
    long id=0;
    private ObservableList<ObservableList> data;
    @FXML
    TextField name=new TextField();
    @FXML
    TableView tableView=new TableView();
    @FXML
    TextField age=new TextField();
    @FXML
    TextField source=new TextField();
    @FXML
    TextField dest=new TextField();
    ActionEvent event;
    @FXML
    Button add=new Button();    
    @FXML
    private TextField ticketid;
    @FXML
    private TextField gender;
    @FXML
    private TextField mobile;
    @FXML
    private TextField uid;
    @FXML
    private TextField date;
    
      public void buildData() {
        data = FXCollections.observableArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/busreservation","root","root");  
            Statement stmt = con.createStatement();  
            ResultSet rs=stmt.executeQuery("select  tid,p.uid,name,Tdate,age,gender,RegNo,r.routeid,source,destination,fare from Passenger as p INNER JOIN Route as r on p.routeid=r.routeid INNER JOIN Ticket as t on p.uid=t.UID order by tid;");  
             tableView.getColumns().clear();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                
                tableView.getColumns().addAll(col);
            }
 
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {                    
                    row.add(rs.getString(i));
                }
                data.add(row); 
            }
            tableView.setItems(data);   
            con.close();
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }
    @FXML
    private void addb(ActionEvent event) throws SQLException {
    try{  
        Class.forName("com.mysql.jdbc.Driver");  
        Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/busreservation","root","root");  
        String src=source.getText(),dst=dest.getText(),regno,rid;
        Statement stmt1 = con.createStatement();  
        ResultSet rs1=stmt1.executeQuery(" select RegNo,Route.RouteID,Source,Destination from Route INNER JOIN Bus on Route.RouteID=Bus.RouteID where source='"+src+"' and destination='"+dst+"';"); 
        boolean flag=false;
        while(rs1.next())
        {
                regno=rs1.getString(1);
                rid=rs1.getString(2);                    
                flag=true;
                
                stmt1.executeUpdate("insert into Passenger values("+ uid.getText()+",'"+name.getText()+"',"+age.getText()+",'"+gender.getText()+"','"+regno+"',"+rid+");");
                stmt1.executeUpdate("insert into Ticket(Tdate,uid) values('"+date.getText()+"',"+uid.getText()+");");
                String[] mobileno=mobile.getText().split(",");
                for(int i=0;i<mobileno.length;i++)
                {                    
                    stmt1.executeUpdate("insert into Mobile values("+uid.getText()+" ,"+Long.parseLong(mobileno[i])+");");
                }                
                ResultSet rs2=stmt1.executeQuery("select TID from Ticket where UID="+uid.getText()+";");
                while(rs2.next())
                {
                    id=Long.parseLong(rs2.getString(1)); 
                    break;
                }
                
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succesful");
                alert.setHeaderText(null);
                alert.setContentText("Ticket booked succesfully with reference number "+id);

                alert.showAndWait();
                clearb(event);
                displayb(event);            
                break;                             
        }
        if(flag==false)
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Details/Route not Found");
            alert.showAndWait();
        }
        con.close();
    }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        displayb(event);
        // TODO
    }    


    @FXML
    private void searchb(ActionEvent event) {
    try{  
        Class.forName("com.mysql.jdbc.Driver");  
        Connection con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/busreservation","root","root");  
        clearb(event);
        String src=source.getText(),dst=dest.getText();
        Statement stmt1 = con.createStatement();         
        ResultSet rs1=stmt1.executeQuery("select * from Passenger INNER JOIN Ticket on Passenger.uid=Ticket.uid INNER JOIN Route on Passenger.Routeid=Route.routeid where tid="+Long.parseLong(ticketid.getText())+";"); 
                while(rs1.next())
                { 
                    uid.setText(rs1.getString(1));
                    name.setText(rs1.getString(2));
                    age.setText(rs1.getString(3));
                    gender.setText(rs1.getString(4));
                    date.setText(rs1.getString(8));           
                    source.setText(rs1.getString(11));
                    dest.setText(rs1.getString(12));
                    Statement stmt2 = con.createStatement(); 
                    ResultSet rs3=stmt2.executeQuery("SELECT UID,mobileno from mobile where uid="+uid.getText()+"; ");
                    while(rs3.next())
                    { mobile.setText(mobile.getText()+""+rs3.getString(2)+",");
                    
                    }
                    
        
        
                    
                }
                if(uid.getText().isEmpty())
                {
                    Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Ticket ID");

            alert.showAndWait();
                }
        
        con.close();
        
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
    }

    @FXML
    private void displayb(ActionEvent event) {
        clearb(event);
        buildData();
    }
    @FXML
    private void clearb(ActionEvent event) {
        name.clear();
        age.clear();
        uid.clear();
        gender.clear();
        source.clear();
        dest.clear();
        date.clear();
        mobile.clear();
    }

    @FXML
    private void deleteb(ActionEvent event) {        
    try {  
        Class.forName("com.mysql.jdbc.Driver");
        Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/busreservation","root","root");  
        Statement stmt1 = con.createStatement();  
        searchb(event);
        stmt1.executeUpdate("delete from Passenger where uid="+uid.getText()+";");
        clearb(event);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succesful");
        alert.setHeaderText(null);
        alert.setContentText("Ticket Cancelled Succesfully");
        alert.showAndWait();
        con.close();
        ticketid.clear();
        
        } catch (Exception e) {
            System.out.println(e);
        }
        displayb(event);

    }
    
}
