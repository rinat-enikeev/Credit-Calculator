package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import su.ugatu.moodle.is.credit_calc.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class Controller {
    private CreditApplication application;
    private CreditOffer offer;
    // Коллекция данных о кредите
    private ObservableList<CreditData> creditData = FXCollections.observableArrayList();
    // Коллекция типы данных о кредите
    private ObservableList<String> model = FXCollections.observableArrayList();

    @FXML
    private ImageView im_exit; // Текстура кнопки выхода
    @FXML
    private ImageView im_help; // Текстура кнопки справки
    @FXML
    private Text text_res; // Текст результата эффективный процентный ставки
    @FXML
    private ComboBox combo_type; // Элемент выбора схем погашения
    @FXML
    private TableView<CreditData> table; // Таблица результатов расчета
    @FXML
    private TableColumn<CreditData,Integer> num; // Колонка номер платежа
    @FXML
    private TableColumn<CreditData,String> paymentDateCol; // Колонка Дата платежа
    @FXML
    private TableColumn<CreditData,String> amountClo; // Колонка Сумма платежа
    @FXML
    private TableColumn<CreditData,String> principalCol; // Колонка Основной долг
    @FXML
    private TableColumn<CreditData,String> accruedInterestCol; // Колонка Начисленные проценты
    @FXML
    private TableColumn<CreditData,String> monthlyCommissionCol; // Колонка Ежемесячные комиссии
    @FXML
    private TableColumn<CreditData,String> balancePayableCol; // Колонка Остаток задолженности
    @FXML
    private TextField amount; // Текстовое поле Сумма кредита
    @FXML
    private TextField durationInMonths; // Текстовое поле  Срок кредита
    @FXML
    private TextField interestRate; // Текстовое поле  Процентная ставка
    @FXML
    private TextField onceCommissionAmount; // Текстовое поле  Разовая комиссия (в деньгах)
    @FXML
    private TextField onceCommissionPercent; // Текстовое поле  Разовая комиссия (в процентах)
    @FXML
    private TextField monthlyCommissionAmount; // Текстовое поле  Ежемесячная комиссия (в деньгах)
    @FXML
    private TextField monthlyCommissionPercent; // Текстовое поле  Ежемесячная комиссия (в процентах)

    @FXML
    private void initialize() {
        // Инициализация текстур
        im_help.setImage(new Image("img/help.png"));
        im_exit.setImage(new Image("img/exit_def.png"));

        model.addAll("Аннуитетная", "Дифференцированная");
        combo_type.setPrefSize(298, 28);
        combo_type.setItems(model); // Добавление данные схем погашения
        combo_type.getSelectionModel().select(0); // Начальная состояния "Аннуитетная"
        // Инициализация текста
        text_res.setText("Кредитный калькулятор позволяет\n" +
                         "построить график погашения\n" +
                         "произвольного кредита,а также\n" +
                         "рассчитать эффективную процентную\n" +
                         "ставку по кредиту.");

        // Устанавливаем тип и значение которое должно хранится в колонке
        num.setCellValueFactory(new PropertyValueFactory<CreditData, Integer>("num"));
        paymentDateCol.setCellValueFactory(new PropertyValueFactory<CreditData, String>("paymentDateCol"));
        amountClo.setCellValueFactory(new PropertyValueFactory<CreditData, String>("amountClo"));
        principalCol.setCellValueFactory(new PropertyValueFactory<CreditData, String>("principalCol"));
        accruedInterestCol.setCellValueFactory(new PropertyValueFactory<CreditData, String>("accruedInterestCol"));
        monthlyCommissionCol.setCellValueFactory(new PropertyValueFactory<CreditData, String>("monthlyCommissionCol"));
        balancePayableCol.setCellValueFactory(new PropertyValueFactory<CreditData, String>("balancePayableCol"));

        // Заполняем таблицу данными
        table.setItems(creditData);
    }

    // Метод выбора схему погашения
    private void initData() {

        if(combo_type.getSelectionModel().isSelected(1)){
            application.setPaymentType(CreditPaymentType.DIFFERENTIAL); // Выбрана "Дифференцированная"
            CreditProposal proposal = offer.calculateProposal(application);
            printProposal(proposal); // Печать результата
        }
        else{
            application.setPaymentType(CreditPaymentType.ANNUITY);// Выбрана "Аннуитетная"
            CreditProposal proposal = offer.calculateProposal(application);
            printProposal(proposal); // Печать результата
        }
    }

    @FXML
    public void onClickMethod(){ // Метод расчёта
        creditData.clear(); // Очистка таблицы
        if(control(amount) && control(durationInMonths) && control(interestRate)){ // Если все три поле числа то..
            application = new CreditApplicationImpl(new BigDecimal(amount.getText())); // Возвращение текста сумма кредита в расчет
            application.setDurationInMonths(Integer.valueOf(durationInMonths.getText())); // Возвращение текста срок кредита в расчет
            offer = new CreditOfferImpl();
            offer.setRate(new BigDecimal(interestRate.getText()).divide(new BigDecimal(100)));// Возвращение текста процентная савка в расчет

            // Проверки на числа комиссии
            if(control(onceCommissionAmount))
                offer.setOnceCommissionAmount(new BigDecimal(onceCommissionAmount.getText()));
            else
                onceCommissionAmount.setText("0");
            if(control(onceCommissionPercent))
                offer.setOnceCommissionPercent(new BigDecimal(onceCommissionPercent.getText()).divide(new BigDecimal(100)));
            else
                onceCommissionPercent.setText("0");
            if(control(monthlyCommissionPercent))
                offer.setMonthlyCommissionPercent(new BigDecimal(monthlyCommissionPercent.getText()).divide(new BigDecimal(100)));
            else
                monthlyCommissionPercent.setText("0");
            if(control(monthlyCommissionAmount))
                offer.setMonthlyCommissionAmount(new BigDecimal(monthlyCommissionAmount.getText()));
            else
                monthlyCommissionAmount.setText("0");

            initData(); // Метод выбора схему погашения
        }

    }
    @FXML
    public void onExit(){ // Метод выхода из программы
        System.exit(0);
    }

    @FXML
    public void onClickHelp() throws Exception { // Метод вызова справки
        new HelpDialog(); // Объект окна справки
    }

    @FXML
    public void onHover(){ // Метод наведение курсора на кнопку выхода
        im_exit.setImage(new Image("img/exit.png"));
        im_help.setImage(new Image("img/help_exit.png"));
    }
    @FXML
    public void onHotHover(){ // Метод деактивированные курсора на кнопку выхода
        im_exit.setImage(new Image("img/exit_def.png"));
        im_help.setImage(new Image("img/help.png"));
    }

    @FXML
    public void onHoverHelp(){ // Метод наведение курсора на кнопку справки
        im_help.setImage(new Image("img/help_hover.png"));
    }
    @FXML
    public void onHotHoverHelp(){ // Метод деактивированные курсора на кнопку справки
        im_help.setImage(new Image("img/help.png"));
    }

    // Метод проверки на число
    private boolean control(TextField tf){
        double d;
        try {
            d = Double.parseDouble(tf.getText());
            if(d < 0) tf.setText(""+Math.abs(d)); // Если введен отрицательный число то берется по модулю
        }
        catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private void printProposal(CreditProposal proposal){ // Метод печать результата
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");// Форматирования даты
        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Форматирования числа
        List<CreditPayment> payments = proposal.getPayments();
        text_res.setText("Всего: "+decimalFormat.format(proposal.getTotalPayment())+" денежных единиц\n"
                        +"Эффективная процентная ставка: "+(decimalFormat.format(proposal.getEffectiveRate().doubleValue() * 100)) + "%\n"
                        +"Комиссия: " + decimalFormat.format(proposal.getInitialCreditCommission()));
        int i = 1;
        // Вывод результатов на таблицу
        for (CreditPayment payment: payments) {
            creditData.add(new CreditData(
                    (i++),
                    "" + dateFormat.format(payment.getDate()),
                    "" + decimalFormat.format(payment.getAmount()),
                    "" + decimalFormat.format(payment.getDebt()),
                    "" + decimalFormat.format(payment.getInterest()),
                    "" + decimalFormat.format(payment.getCommission()),
                    "" + decimalFormat.format(payment.getTotalLeft())));
        }

    }

}
