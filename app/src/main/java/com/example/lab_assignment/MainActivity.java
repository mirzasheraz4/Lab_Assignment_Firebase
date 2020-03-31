package com.example.lab_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore objectFirebaseFirestore;
    private DocumentReference objectDocumentReference;
    private CollectionReference objectCollectionReference;

    private static final String COLLECTION_NAME="Products";

    private Dialog objectDialog;
    private EditText productIDET,productNameET,productPriceET;
    private TextView DataTV;
    private String CompleteData ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectFirebaseFirestore=FirebaseFirestore.getInstance();
        objectDialog=new Dialog(this);

        objectDialog=new Dialog(this);
        objectDialog.setContentView(R.layout.please_wait);



        productIDET=findViewById(R.id.productIDET);
        productNameET=findViewById(R.id.productNameET);

        productPriceET=findViewById(R.id.productPriceET);

        DataTV = findViewById(R.id.DataTV);
        DataTV.setMovementMethod(new ScrollingMovementMethod());

        try {
            objectFirebaseFirestore=FirebaseFirestore.getInstance();
            objectCollectionReference=objectFirebaseFirestore.collection(COLLECTION_NAME);
        }
        catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
       // downloadedDataTV=findViewById(R.id.downloadedDataTV);
    }
    public void addValuesToFirebaseFirestore(View view)
    {
        try {
            objectFirebaseFirestore = FirebaseFirestore.getInstance();
            objectFirebaseFirestore.collection(COLLECTION_NAME).document(productIDET.getText().toString()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {
                                Toast.makeText(MainActivity.this, "Already Created with this ID", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                if(!productIDET.getText().toString().isEmpty() && !productNameET.getText().toString().isEmpty() && !productPriceET.getText().toString().isEmpty()) {
                                    objectDialog.show();
                                    Map<String,Object> objMap=new HashMap<>();
                                    objMap.put("Name", productNameET.getText().toString());
                                    objMap.put("Price", productPriceET.getText().toString());
                                    objectFirebaseFirestore.collection(COLLECTION_NAME)
                                            .document(productIDET.getText().toString()).set(objMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    objectDialog.dismiss();
                                                    productIDET.setText("");
                                                    productPriceET.setText("");

                                                    productNameET.setText("");
                                                    productIDET.requestFocus();
                                                    objectDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    objectDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Failed To Add Data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else if(productIDET.getText().toString().isEmpty())
                                {
                                    Toast.makeText(MainActivity.this, "Please enter product id", Toast.LENGTH_SHORT).show();
                                }

                                else if(productNameET.getText().toString().isEmpty())
                                {
                                     Toast.makeText(MainActivity.this, "Please enter product name", Toast.LENGTH_SHORT).show();
                                }
                                else if(productPriceET.getText().toString().isEmpty())
                                {
                                     Toast.makeText(MainActivity.this, "Please enter product price", Toast.LENGTH_SHORT).show();
                                 }
                            }
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Add Values"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void  getAllDataFromFirebaseFirestore(View v)
    {
        try
        {
            CompleteData ="";
            objectDialog.show();
            DataTV.setText("");
            objectDialog.show();
            objectCollectionReference.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            objectDialog.dismiss();
                            for (DocumentSnapshot objectDocumentReference : queryDocumentSnapshots) {
                                String ProductID = objectDocumentReference.getId();
                                String ProductName = objectDocumentReference.getString("Name");
                                String ProductPrice = objectDocumentReference.getString("Price");
                                CompleteData += "Product ID: "
                                        + ProductID + '\n' + "Product Name: "
                                        + ProductName + '\n' + "Product Price: "
                                        + ProductPrice +'\n'+"_____________________________"+'\n' ;
                            }
                            DataTV.setText(CompleteData);
                            Toast.makeText(MainActivity.this,"Retrieve Data Successfully",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Fails to retrieve data:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void  deleteDocumentFromFirebaseFireStore(View V)
    {
        try
        {
            if(productIDET.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please enter the document id", Toast.LENGTH_SHORT).show();
            }
            else {
                if (!productIDET.getText().toString().isEmpty()) {
                    objectDocumentReference = objectFirebaseFirestore.collection(COLLECTION_NAME)
                            .document(productIDET.getText().toString());
                    objectDocumentReference.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    objectDialog.dismiss();
                                    productIDET.setText("");
                                    Toast.makeText(MainActivity.this, "Document Deleted", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    objectDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Failed to Delete The Document", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Failed to Delete The Document", Toast.LENGTH_LONG);
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void  deletecollection(String ID)
    {
        try
        {
            objectDocumentReference = objectFirebaseFirestore.collection(COLLECTION_NAME)
                    .document(ID);
            objectDocumentReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Collection Deleted Successfully",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Failed to Delete the Collection",Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void deleteCollectionFromFirebaseFireStore(View v)
    {
        objectDialog.show();
        objectCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        objectDialog.dismiss();
                        for (DocumentSnapshot objectDocumentReference : queryDocumentSnapshots) {
                            String ProductID = objectDocumentReference.getId();
                            deletecollection(ProductID);
                        }
                        Toast.makeText(MainActivity.this,"Collection Deleted Successfully",Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                objectDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed to Delete the Collection" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
