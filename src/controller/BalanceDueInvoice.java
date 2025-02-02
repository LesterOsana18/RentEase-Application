package controller;

public class BalanceDueInvoice {
    private String property;
    private String unit;
    private String date;
    private String billType;
    private double amount;
    private double deposit;
    private double advance;
    private String status;
    private String receipt;

    public BalanceDueInvoice(String property, String unit, String date, String billType,
                              double amount, double deposit, double advance,
                              String status, String receipt) {
        this.property = property;
        this.unit = unit;
        this.date = date;
        this.billType = billType;
        this.amount = amount;
        this.deposit = deposit;
        this.advance = advance;
        this.status = status;
        this.receipt = receipt;
    }

    // Getters and setters
    public String getProperty() { 
    	return property; 
    }
    public void setProperty(String property) { 
    	this.property = property; 
    }

    public String getUnit() { 
    	return unit; 
    }
    public void setUnit(String unit) { 
    	this.unit = unit; 
    }

    public String getDate() { 
    	return date; 
    }
    public void setDate(String date) { 
    	this.date = date; 
    }

    public String getBillType() { 
    	return billType; 
    }
    public void setBillType(String billType) { 
    	this.billType = billType; 
    }

    public double getAmount() { 
    	return amount; 
    }
    public void setAmount(double amount) { 
    	this.amount = amount; 
    }

    public double getDeposit() { 
    	return deposit; 
    	}
    public void setDeposit(double deposit) { 
    	this.deposit = deposit; 
    }

    public double getAdvance() { 
    	return advance; 
    }
    public void setAdvance(double advance) { 
    	this.advance = advance; 
    }

    public String getStatus() { 
    	return status; 
    }
    public void setStatus(String status) { 
    	this.status = status; 
    }
    
    public String getReceipt() {
    	return receipt;
    }
    public void setReciept(String receipt) {
    	this.receipt = receipt;
    }
    
}
