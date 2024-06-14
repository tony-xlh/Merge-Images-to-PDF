//
//  ViewController.swift
//  PDFCreator
//
//  Created by 徐力航 on 2024/6/14.
//

import UIKit

class ViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return 3
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int,
                        forComponent component: Int) -> String? {
        if row == 0 {
            return "Black & White"
        }else if row == 1 {
            return "Grayscale"
        }else{
            return "Color"
        }
                
    }
    
    @IBOutlet weak var selectImagesUIButton: UIButton!
    
    @IBOutlet weak var colorModeUIPickerView: UIPickerView!
    @IBOutlet weak var enableAutoCroppingUISwitch: UISwitch!
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        colorModeUIPickerView.dataSource = self
        colorModeUIPickerView.delegate = self
    }

    @IBAction func selectImagesUIButton_clicked(_ sender: Any) {
        print("clicked")
    }
    
}

